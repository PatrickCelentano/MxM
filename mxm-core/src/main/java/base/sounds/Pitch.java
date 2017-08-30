package base.sounds;

import base.analysis.Interval;
import io.MxmLog;
import base.relative.PitchClass;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Pitch is a simple class which utilizes the interning design pattern to create only one
 * hundred twenty different values- all possible MIDI pitches. Pitches are usually found on
 * Notes, though they may be used in Collections. passage.passage.musicEvents.sounding.Note that there should never be more than
 * these 120 Pitches, and that an iterator() has been provided for easy access.
 */
public class Pitch implements ISound, Comparator<Pitch>, Comparable<Pitch> {

    //////////////////////////////
    // Private static variables //
    //////////////////////////////

    /** The minimum midi sound value, C-1. */
    private static final int MIN_PITCH_VALUE = 0;
    /** The maximum midi sound value, B9. */
    private static final int MAX_PITCH_VALUE = 120;
    /** An ArrayList of all valid Pitches */
    private static final ArrayList<Pitch> ALL = new ArrayList<>();

    static {
        // A bunch of useful logging of the initialization process
        MxmLog.logHeader("PITCH");
        MxmLog.log("Initialization started",1);
        ArrayList<String> allInitStr = new ArrayList<>();
        allInitStr.add("Initialized");

        // Initialize all Pitches
        for(int value = MIN_PITCH_VALUE; value <= MAX_PITCH_VALUE; value++) {
            Pitch pitch = new Pitch(value);
            ALL.add(pitch);
            allInitStr.add(pitch.toString());
        }

        // More useful logging, after the initialization is done
        MxmLog.log(allInitStr,1);
        MxmLog.log("Initialization completed",1);
        MxmLog.logFooter();
    }

    //////////////////////////////
    // Public static variables  //
    //////////////////////////////

    /** The minimum octave, -1 */
    public static final int MIN_OCTAVE = -1;
    /** The maximum octave, 9 */
    public static final int MAX_OCTAVE = 9;
    /** The lowest possible pitch */
    public static final Pitch MIN_PITCH = get(MIN_PITCH_VALUE);
    /** The highest possible pitch */
    public static final Pitch MAX_PITCH = get(MAX_PITCH_VALUE);

    //////////////////////////////
    //      Static methods      //
    //////////////////////////////

    /**
     * Gets an iterator which enumerates all valid Pitches.
     * @return An iterator over all valid Pitches.
     */
    public static Iterator<Pitch> allItr() {
        return ALL.iterator();
    }

    /**
     * Gets an instance of a given pitch. This method creates the interning design pattern per pitch.
     * @param value The (midi) value of this pitch.
     * @return An pitch of this (midi) value
     */
    public static Pitch get(int value) {
        if(value >= MIN_PITCH_VALUE && value <= MAX_PITCH_VALUE )
            return ALL.get(value - MIN_PITCH_VALUE);
        else
            throw new Error("INTERVAL:\tInterval out of range.");
    }

    /**
     * Gets an instance of a given pitch. This method creates the interning design pattern per pitch.
     * @param pitchClass The pitch class of this pitch (C, Ab, F#)
     * @param octave The octave of this pitch
     * @return A pitch of this pitch class and octave
     */
    public static Pitch get(@NotNull PitchClass pitchClass, int octave) {
        int value = pitchClass.getValue()+ (octave + MIN_OCTAVE)*12;
        if(value >= MIN_PITCH_VALUE && value <= MAX_PITCH_VALUE )
            return ALL.get(value - MIN_PITCH_VALUE);
        else
            throw new Error("PITCH:\tPitch out of range.");
    }

    //////////////////////////////
    // Private member variables //
    //////////////////////////////

    /** The sound class of this pitch */
    private PitchClass pitchClass;
    /** The octave of this pitch, between OCTAVE_MIN and OCTAVE_MAX */
    private int octave;
    /** The midi value of this pitch, between PITCH_MIN and PITCH_MAX */
    private int value;

    //////////////////////////////
    //   Private constructor    //
    //////////////////////////////

    /**
     * The pitch constructor, which is private to enforce the interning design pattern (one instance per value).
     * @param value The (midi) value of this pitch
     */
    private Pitch(int value) {
        this.value = value;
        this.pitchClass = PitchClass.get(value%12);
        this.octave = value/12;
    }

    //////////////////////////////
    //   Public member methods  //
    //////////////////////////////

    /**
     * Gets the (midi) value of this pitch.
     * @return The (midi) value of this pitch
     */
    public int getValue() {
        return value;
    }

    /**
     * Gets the pitch class of this pitch.
     * @return The pitch class of this pitch
     */
    public PitchClass getPitchClass() {
        return pitchClass;
    }

    /**
     * Gets the octave of this pitch.
     * @return The octave of this pitch
     */
    public int getOctave() {
        return octave;
    }

    /**
     * Transposes a pitch by a given interval
     * @param interval The interval to transpose by
     * @return The new, resulting pitch
     */
    public Pitch transpose(Interval interval) {
        return new Pitch(value + interval.getSize());
    }

    /**
     * Gets the interval between this pitch and another.
     * @param other The other pitch to subtract from this one
     * @return The interval between this pitch and another
     */
    public Interval minus(Pitch other) {
        return Interval.getInstance(other.value - this.value);
    }

    /**
     * Normalizes this pitch between 0 and 1.
     * @return The value of this pitch in the range [0,1).
     */
    public float normalized() {
        return (float)(value - MIN_PITCH_VALUE)/(float)(MAX_PITCH_VALUE - MIN_PITCH_VALUE);
    }

    /**
     * Returns a nicely-formatted string of this pitch (for debug).
     * @return A nicely-formatted string of this pitch
     */
    public String toString() {
        return pitchClass.toString() + (value/12 - 1);
    }

    /**
     * Compares this pitch to another pitch.
     * @param other The other pitch
     * @return The comparison
     */
    @Override
    public int compareTo(Pitch other) {
        return new Integer(value).compareTo(new Integer(other.value));
    }

    /**
     * Compares two pitches.
     * @param p1 The first pitch
     * @param p2 The second pitch
     * @return The comparison
     */
    @Override
    public int compare(Pitch p1, Pitch p2) {
        return new Integer(p1.value).compareTo(new Integer(p2.value));
    }

    /**
     * Checks if this pitch equals another object.
     * @param object The other object
     * @return If this pitch is equal to the object
     */
    @Override
    public boolean equals(Object object) {
        return this == object;
    }

    /**
     * A simple hash code for storage of pitches in special collections.
     * @return The hash code for this pitch
     */
    @Override
    public int hashCode() {
        return value;
    }
}