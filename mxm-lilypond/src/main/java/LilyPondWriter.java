import analysis.RhythmNode;
import analysis.RhythmTree;
import base.Count;
import base.TimeSignature;
import form.Passage;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * This writer has a number of recursive methods that
 */
public class LilyPondWriter {

     /**
     * Write takes a passage and writes it to a file of the given name.
     * @param passage The passage to write to a .ly file
     * @param filename The name of the file (before .ly is added)
     */
    public static void write(Passage passage, String filename) {
        try {
            PrintWriter writer = new PrintWriter(filename + ".ly", "UTF-8");
            writer.println(header("t","s")+writePassage("Passage 1"));
            writer.close();
        }
        catch (IOException e) {
            // do something
        }
    }

    public static String header(String title, String subtitle) {
        return  "\\version \"2.18.2\"\n" +
                "\\header {\n" +
                "\ttitle = \"" + title + "\"\n" +
                "\tsubtitle = \"" + subtitle + "\"\n" +
                "\tcomposer = \"Music ex Machina\"\n" +
                "\ttagline = \"This piece was generated by MxM and engraved by LilyPond.\"\n" +
                "\tcopyright = \"All Rights Reserved\"\n"+
                "}\n";
    }

    public static String writePassage(String passageName){
        return "\\score { \n" +
                "\t\\new Staff {\n" +

                "\t}\n" +
                "\t\\header {\n" +
                "\t\tpiece = \"" + passageName + "\"\n" +
                "\t}\n" +
                "}";
    }

    public static String rhythmTreeToLilyPond() {
        TimeSignature timeSignature = TimeSignature.getInstance(3,4);
        RhythmTree rhythmTree = new RhythmTree(new int[]{2,0,0});
        System.out.println(rhythmTree);

        String lilyPondString = "\\version \"2.18.2\" \n \\score{ \n { ";
        lilyPondString += "\t\\time " + timeSignature.getNumerator() + "/" + timeSignature.getDenominator() + "\n";
        lilyPondString += "\t" + getLilyPondString(rhythmTree.getRoot(),timeSignature);
        return lilyPondString + " }\n\n\\layout{}\n\\midi{}\n}";
    }

    public static String getLilyPondString(RhythmNode node, TimeSignature timeSignature) {
        String toReturn = "";

        // If this is a leaf node
        if(node.getValue() == 0) {
            Count nodeSize = timeSignature.getMeasureSize();

            nodeSize = timeSignature.getPreferredNoteLength(node.getDepth());

            /*
            nodeSize = nodeSize.times(node.getDuration().getDenominator());
            nodeSize = nodeSize.dividedBy(node.getDuration().getNumerator());
            nodeSize = new Count(nodeSize.getNumerator()%nodeSize.getDenominator(),nodeSize.getDenominator());
            System.out.println(nodeSize);
            */


            if(nodeSize.getNumerator() == 1) {
                toReturn += " c'"+timeSignature.getPreferredNoteLength(node.getDepth()).getDenominator();
            }
            else if(nodeSize.getNumerator() == 3) {
                toReturn += " c'"+timeSignature.getPreferredNoteLength(node.getDepth()).getDenominator()/2+".";
            }
            else System.out.println("!");
        }
        else {
            int preferredSubdivision    = timeSignature.getPreferredSubdivision(node.getDepth());
            int actualSubdivision       = node.getValue();

            //System.out.println(preferredSubdivision + " vs " + actualSubdivision);
            // Tuplets
            if(preferredSubdivision % actualSubdivision != 0) {
                toReturn += " \\tuplet " + actualSubdivision + "/" + preferredSubdivision + " {";
                for(RhythmNode child : node.getChildren()) {
                    toReturn += getLilyPondString(child,timeSignature);
                }
                toReturn += "}";
            }
            else {
                for(RhythmNode child : node.getChildren()) {
                    toReturn += getLilyPondString(child,timeSignature);
                }
            }
        }

        return toReturn;
    }

    public static void main(String args[]) {
        //System.out.println(rhythmTreeToLilyPond());
        //engrave();
        write(null,"test");
        LilyPondTools.engrave("test.ly");
    }
}