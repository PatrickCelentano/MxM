from __future__ import print_function, division
import tensorflow as tf
import numpy as np


def randomize(dataset, labels):
	permutation = np.random.permutation(labels.shape[0])
	shuffled_dataset = dataset[permutation, :, :]
	shuffled_labels = labels[permutation]
	return shuffled_dataset, shuffled_labels


def one_hot_encode(np_array):
	return (np.arange(10) == np_array[:, None]).astype(np.float32)


def reformat_data(dataset, labels, image_width, image_height, image_depth):
	np_dataset_ = np.array(
		[np.array(image_data).reshape(image_width, image_height, image_depth) for image_data in dataset])
	np_labels_ = one_hot_encode(np.array(labels, dtype=np.float32))
	np_dataset, np_labels = randomize(np_dataset_, np_labels_)
	return np_dataset, np_labels


def flatten_tf_array(array):
	shape = array.get_shape().as_list()
	return tf.reshape(array, [shape[0], shape[1] * shape[2] * shape[3]])


def accuracy(predictions, labels):
	return 100.0 * np.sum(np.argmax(predictions, 1) == np.argmax(labels, 1)) / predictions.shape[0]


"""
Simple 1d cnn
"""
def train_1d_cnn(train_xs, train_ys, test_xs, test_ys):
	num_steps = 10001
	disp_step = 1000
	eta = 0.5

	graph = tf.Graph()
	with graph.as_default():
		train_xs_r = tf.placeholder(tf.float32, shape=(train_xs.shape[0], train_xs.shape[1], train_xs.shape[2]))
		train_ys_r = tf.placeholder(tf.float32, shape=(train_ys.shape[0], train_ys.shape[1]))
		test_xs_r = tf.constant(test_xs, tf.float32)

		weights = tf.Variable(tf.truncated_normal([train_xs.shape[1] * train_xs.shape[0], train_ys.shape[1]]),
		                      tf.float32)
		bias = tf.Variable(tf.zeros([train_ys.shape[1]]), tf.float32)

		def model(data, weights, bias):
			return tf.matmul(flatten_tf_array(data), weights) + bias

		logits = model(train_xs_r, weights, bias)

		loss = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(logits=logits, labels=train_ys_r))

		optimizer = tf.train.GradientDescentOptimizer(eta).minimize(loss)

		train_prediction = tf.nn.softmax(logits)
		test_prediction = tf.nn.softmax(model(test_xs_r, weights, bias))

	with tf.Session(graph=graph) as session:
		tf.global_variables_initializer().run()
		print('Initialized...\n')
		for step in range(num_steps):
			_, l, predictions = session.run([optimizer, loss, train_prediction])
			if (step % disp_step == 0):
				train_accuracy = accuracy(predictions, train_ys[:, :])
				test_accuracy = accuracy(test_prediction.eval(), test_ys)
				message = "Step {:04d} :\n \t Loss: {:06.2f}\n \t Training Accuracy: {:02.2f} % \n\t Test Accuracy {:02.2f} %".format(
					step, l, train_accuracy, test_accuracy)
				print(message)

# !/usr/bin/env python
"""
Example of using Keras to implement a 1D convolutional neural network (CNN) for timeseries prediction.
"""

from keras.layers import Convolution1D, Dense, MaxPooling1D, Flatten
from keras.models import Sequential

# __date__ = '2016-07-22'


def make_regressor(wsize, filter_size, num_input_series=1, num_outputs=1, num_filt=4):
	""":Return: a Keras Model pred next value in a time series
    :param int wsize: num previous time steps
    :param int num_input_series: number of series;
    :param int num_outputs: number of output logits
    :param int filter_size: filter size
    :param int num_filt: The number of different filters to learn (roughly, input patterns to recognize).
    """
	model = Sequential((
		Convolution1D(nb_filter=num_filt, filter_length=filter_size, activation='relu',
		              input_shape=(wsize, num_input_series)),
		MaxPooling1D(),
		Convolution1D(nb_filter=num_filt, filter_length=filter_size, activation='relu'),
		MaxPooling1D(),
		Convolution1D(nb_filter=num_filt, filter_length=filter_size, activation='relu'),
		MaxPooling1D(),
		Convolution1D(nb_filter=num_filt, filter_length=filter_size, activation='relu'),
		MaxPooling1D(),
		Convolution1D(nb_filter=num_filt, filter_length=filter_size, activation='relu'),
		MaxPooling1D(),
		Flatten(),
		Dense(num_outputs, activation='linear'),
	))
	model.compile(loss='mse', optimizer='adam', metrics=['mae'])
	# model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['binary_accuracy']) #binary classification
	return model


def make_instances(time_series, wsize):
	"""Make input features and prediction targets from a `timeseries` for use in machine learning.
    :return: A tuple of `(X, y, q)`.  `X` are the inputs to a predictor, a 3D ndarray with shape
      ``(timeseries.shape[0] - window_size, window_size, timeseries.shape[1] or 1)``.  For each row of `X`, the
      corresponding row of `y` is the next value in the timeseries.  The `q` or query is the last instance, what you would use
      to predict a hypothetical next (unprovided) value in the `timeseries`.
    :param ndarray time_series: Either a simple vector, or a matrix of shape ``(timestep, series_num)``, i.e., time is axis 0 (the
      row) and the series is axis 1 (the column).
    :param int wsize: The number of samples to use as input prediction features (also called the lag or lookback).
    """
	time_series = np.asarray(time_series)
	assert 0 < wsize < time_series.shape[0]
	X = np.atleast_3d(np.array([time_series[start:start + wsize] for start in range(0, time_series.shape[0] - wsize)]))
	y = time_series[wsize:]
	q = np.atleast_3d([time_series[-wsize:]])
	return X, y, q


def evaluate(time_series, window_size):
	"""Create a 1D CNN regressor to predict the next value in a `timeseries` using the preceding `window_size` elements
    as input features and evaluate its performance.
    :param ndarray time_series: Timeseries data with time increasing down the rows (the leading dimension/axis).
    :param int window_size: The number of previous timeseries values to use to predict the next.
    """
	filter_length = 5
	num_filt = 4
	time_series = np.atleast_2d(time_series)
	if time_series.shape[0] == 1:
		time_series = time_series.T  # Convert 1D vectors to 2D column vectors

	num_samples, num_series = time_series.shape
	print('\n\nPhrases ({} samples by {} series):\n'.format(num_samples, num_series), time_series)
	model = make_regressor(wsize=window_size, filter_size=filter_length, num_input_series=num_series,
	                       num_outputs=num_series, num_filt=num_filt)
	print('\n\nInput Size {}, Output Size {}, {} Conv Filter Size {}'.format(model.input_shape,
	                                                                                          model.output_shape,
	                                                                                          num_filt, filter_length))
	model.summary()

	X, y, q = make_instances(time_series, window_size)
	print('\n\nInput features:', X, '\n\nOutput labels:', y, '\n\nQuery vector:', q, sep='\n')
	test_size = int(0.01 * num_samples)
	train_xs, test_xs, train_ys, test_ys = X[:-test_size], X[-test_size:], y[:-test_size], y[-test_size:]
	model.fit(train_xs, train_ys, nb_epoch=25, batch_size=2, validation_data=(test_xs, test_ys))

	pred = model.predict(test_xs)
	print('\n\nactual', 'predicted', sep='\t')
	for actual, predicted in zip(test_ys, pred.squeeze()):
		print(actual.squeeze(), predicted, sep='\t')
	print('next', model.predict(q).squeeze(), sep='\t')


def main():
	np.set_printoptions(threshold=25)
	ts_length = 1000
	window_size = 50

	print('\nSimple single melody vector prediction')
	timeseries = np.arange(ts_length)  # The timeseries f(t) = t
	evaluate(timeseries, window_size)

main()
