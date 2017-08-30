import io.MxmLog;
import base.sounds.Chord;
import base.sounds.Pitch;
import base.time.Tempo;
import base.time.Time;
import passage.scoreTypes.LeadSheet;
import passage.musicEvents.Note;
import passage.musicEvents.TempoChange;
import passage.musicEvents.TimeSigChange;
import passage.Line;

import static base.relative.ChordClass.*;
import static base.relative.PitchClass.*;

public class Test {

    public static void main(String[] args) {
        // ========================================================================================================= //
        System.out.println("Creating lead sheet");
        LeadSheet leadSheet = new LeadSheet("My lead sheet");
        Line<Pitch> tune = leadSheet.getTune();
        Line<Chord> changes = leadSheet.getChanges();
        // ========================================================================================================= //
        leadSheet.add(new Tempo(120),Time.get(0));
        leadSheet.add(new Tempo(100),Time.get(10));
        leadSheet.add(new Tempo(120),Time.get(32));
        // ========================================================================================================= //
        System.out.println("Creating tune");
        tune.add(Pitch.get(C_NATURAL,4),        Time.get(1,4))
                .add(Pitch.get(D_NATURAL,4),    Time.get(1,4))
                .add(Pitch.get(E_NATURAL,4),    Time.get(1,4))
                .add(Pitch.get(F_NATURAL,4),    Time.get(1,4))
                .add(Pitch.get(G_NATURAL,4),    Time.get(1,4))
                .add(Pitch.get(A_NATURAL,4),    Time.get(1,4))
                .add(Pitch.get(B_NATURAL,4),    Time.get(1,4))
                .add(Pitch.get(C_NATURAL,5),    Time.get(1,4));
        // ========================================================================================================= //
        System.out.println("Creating changes");
        changes.add(Chord.get(C_NATURAL,MAJOR),         Time.get(1,2))
                .add(Chord.get(D_NATURAL,MINOR),        Time.get(1,2))
                .add(Chord.get(G_NATURAL,MAJOR),        Time.get(1,2))
                .add(Chord.get(C_NATURAL,DOM_SEVENTH),  Time.get(1,2))
                .add(Chord.get(C_NATURAL,MAJOR),        Time.get(1));
        // ========================================================================================================= //
        for(TimeSigChange timeSigChange : leadSheet.getTimeSigChanges()) {
            MxmLog.log(timeSigChange.getTimeSig().toString(), 1);
        }
        for(TempoChange tempoChange : leadSheet.getTempoChanges()) {
            MxmLog.log(tempoChange.getTempo().toString(),1);
        }
        // ========================================================================================================= //
        for(Note<Pitch> note : tune) {
            MxmLog.log(note.getSound().toString(),0);
        }
        for(Note<Chord> note : changes) {
            MxmLog.log(note.getSound().toString(),0);
        }
        // ========================================================================================================= //
    }
}