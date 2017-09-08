package form.timelines;

import base.time.Time;
import form.IFrame;
import form.IParallelTimeline;
import form.events.IMusicEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

@SuppressWarnings("unchecked")
final class ParallelTimeline <MusicEventType extends IMusicEvent> implements IParallelTimeline<MusicEventType> {
    private final TreeMap<Time, Frame<MusicEventType>> frames;

    ParallelTimeline() {
        this.frames = new TreeMap<>();
    }

    // PUBLIC GETTERS
    @Override
    public final @NotNull IFrame<MusicEventType> getFirstFrame() { return frames.firstEntry().getValue(); }
    @Override
    public final @NotNull IFrame<MusicEventType> getLastFrame() { return frames.lastEntry().getValue(); }
    @Override
    public final @NotNull IFrame<MusicEventType> getFrameAt(@NotNull Time time) {
        return frames.get(time);
    }
    @Override
    public final @NotNull IFrame<MusicEventType> getFrameBefore(@NotNull Time time) {
        return frames.floorEntry(time).getValue();
    }
    @Override
    public final @NotNull IFrame<MusicEventType> getFrameAfter(@NotNull Time time) {
        return frames.ceilingEntry(time).getValue();
    }

    @Override
    public final @NotNull Iterator<IFrame<MusicEventType>> iterator() {
        Collection constValues = java.util.Collections.unmodifiableCollection(frames.values());
        return constValues.iterator();
    }
}