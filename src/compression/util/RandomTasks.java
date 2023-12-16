package compression.util;

import compression.LocalConfig;
import compression.grammar.SecondaryStructureGrammar;
import compression.samplegrammars.*;

import javax.swing.plaf.ListUI;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomTasks {
    public static void main(String[] args) throws IOException {
        //getRandom10Percent();
        copy100BestGrammars();
        //LiuGrammar lg = new LiuGrammar(true);
        printGrammarInSrf("A0 -> ( A2 )      A1 -> A1 A0  A2 -> ( A2 ) A1 -> A0 A2 -> A1 A0 -> ."
        );
    }
    private static void getRandom10Percent() throws IOException {
        Random rand = new Random(234);
        File folder = new File("C:\\Users\\evita\\Documents\\GitHub\\compressed-rna\\datasets\\Benchmark_without_Empty_Hair_pin\\");
        File[] fileList= folder.listFiles();
        File grammarFile = new File(LocalConfig.GIT_ROOT+"\\datasets\\Benchmark_Random_10percent\\");
        int ten_percent= (int) (fileList.length/10)+1;



        for (int i=0; i<=ten_percent;i++){
                    File newFile = fileList[rand.nextInt(fileList.length)];
                    System.out.println(newFile);
                    //File sourceFile = new File("C:\\Users\\evita\\Documents\\GitHub\\compressed-rna\\datasets\\Benchmark_without_Empty_Hair_pin\\"+newFile+"txt");

                    System.out.println(newFile.getName());
                    File dest = new File(LocalConfig.GIT_ROOT+"\\datasets\\Benchmark_Random_10percent\\"+newFile.getName());

                    //copy file conventional way using Stream
                    //long start = System.nanoTime();
                    copyFileUsingChannel(newFile, dest);
        }

        }
        public static void printGrammarInSrf(String grammarString){
            String newString="";
            for(char i: grammarString.toCharArray()){
                if(i=='(')
                    newString=newString+"\\sso";
                else if(i==')')
                    newString=newString+"\\ssc";
                else if(i=='.')
                    newString=newString+"\\ssu";
                else if(i=='\u2192')
                    newString=newString+"\\rightarrow";
                else if(i==',')
                {}
                else if(i=='\n')
                { newString= newString+"$"+i+"\\\\$";}
                else
                    newString=newString+i;

            }


            System.out.println(newString);

        }

        public static void printGrammarInSrf(SecondaryStructureGrammar ssg){
        String grammarString =ssg.convertToSRF().toString();
        String newString="";
        for(char i: grammarString.toCharArray()){
            if(i=='(')
                newString=newString+"\\sso";
            else if(i==')')
                newString=newString+"\\ssc";
            else if(i=='.')
                newString=newString+"\\ssu";
            else if(i=='\u2192')
                newString=newString+"\\rightarrow";
            else if(i==',')
            {}
            else if(i=='\n')
            { newString= newString+"$"+i+"\\\\$";}
            else
                newString=newString+i;

        }


        System.out.println(newString);
        }

    public static void copyFiles() throws IOException {
        File desination = new File("C:/Users/evita/Documents/GitHub/compressed-rna/src/GrammarGenerator/Small_Dataset");
        File source = new File("C:/Users/evita/Documents/GitHub/compressed-rna/datasets/friemel-modified");
        String[] files_to_select = {"3892_120_c.txt", "3988_119_c.txt","12234_115_c.txt","4008_119_c.txt", "9732_121_c.txt", "925_122_c.txt",
                "10699_120_c.txt","13827_120_c.txt", "13748_119_c.txt","14892_119_c.txt","14250_118_c.txt", "14722_123_c.txt", "1182_122_c.txt",
                "863_119_c.txt","201_120_c.txt", "1795_118_c.txt", "1044_122_c.txt", "4906_119_c.txt", "8857_121_c.txt", "4350_120_c.txt", "3096_21_c.txt",
                "12157_23_c.txt", "363_22_c.txt", "2989_24_c.txt", "16694_20_c.txt", "16731_22_c.txt", "3730_16_c.txt", "12954_21_c.txt",
                "7052_16_c.txt", "16783_18_c.txt", "15564_21_c.txt", "7527_15_c.txt", "7205_14_c.txt", "2096_17_c.txt", "523_16_c.txt",
                "7883_16_c.txt", "12347_8_c.txt", "4266_8_c.txt", "437_8_c.txt", "456_2_c.txt", "2220_35_c.txt", "5006_30_c.txt",
                "3135_22_c.txt","12690_22_c.txt", "5340_45_c.txt", "2992_23_c.txt", "2462_25_c.txt", "16553_16_c.txt", "10090_41_c.txt",
                "6554_25_c.txt",  "9488_25_c.txt"," 266_35_c.txt", "3552_31_c.txt", "13007_45_c.txt", "5720_45_c.txt", "12084_32_c.txt", "16439_25_c.txt",
                "15060_40_c.txt", "983_60_c.txt", "456_2_c.txt", "8460_112_c.txt", "7917_117_c.txt", "15639_121_c.txt", "14542_119_c.txt", "165_120_c.txt",
                "4120_113_c.txt", "14931_120_c.txt", "13010_117_c.txt", "7217_122_c.txt", "13508_118_c.txt", "11608_118_c.txt", "15471_120_c.txt",
                "4646_120_c.txt",  "1850_117_c.txt","10457_115_c.txt",  "14329_120_c.txt",  "10023_120_c.txt", "7802_118_c.txt","2727_113_c.txt","1593_121_c.txt"};

        String[] list_from_sourcefile = source.list();

        for (int i=0; i<files_to_select.length;i++){
            for(int j=0; j<list_from_sourcefile.length; j++){
                if(files_to_select[i].equals(list_from_sourcefile[j])){
                    File sourceFile = new File("C:/Users/evita/Documents/GitHub/compressed-rna/datasets/friemel-modified/"+list_from_sourcefile[j]);
                    File dest = new File("C:/Users/evita/Documents/GitHub/compressed-rna/src/GrammarGenerator/Small_Dataset/"+files_to_select[i]);

                    //copy file conventional way using Stream
                    //long start = System.nanoTime();
                    copyFileUsingChannel(sourceFile, dest);
                }
            }

        }
    }

    public static void copy100BestGrammars() throws IOException {

        File source = new File("C:/Users/evita/Documents/GitHub/compressed-rna/grammars/grammars_first_filter");

        String[] files={"grammar-2NTs-5rules-2574.txt","grammar-2NTs-5rules-2599.txt","grammar-2NTs-6rules-6526.txt","grammar-2NTs-5rules-2602.txt",
            "grammar-2NTs-5rules-2605.txt","grammar-2NTs-6rules-6825.txt","grammar-2NTs-6rules-5291.txt","grammar-2NTs-6rules-6559.txt","grammar-2NTs-6rules-6562 .txt","grammar-2NTs-6rules-6569.txt","grammar-2NTs-7rules-1081.txt",
                "grammar-2NTs-6rules-5316.txt","grammar-2NTs-7rules-12616.txt","grammar-2NTs-5rules-2571.txt","grammar-2NTs-7rules-14348.txt",
                "grammar-2NTs-6rules-7861.txt","grammar-2NTs-8rules-21303.txt","grammar-2NTs-5rules-2680.txt","grammar-2NTs-6rules-5783.txt","grammar-2NTs-6rules-6113.txt",
                "grammar-2NTs-6rules-6407.txt","grammar-2NTs-6rules-6463.txt","grammar-2NTs-6rules-6535.txt","grammar-2NTs-6rules-6523.txt","grammar-2NTs-8rules-21292.txt","grammar-2NTs-7rules-14338.txt",
                "grammar-2NTs-7rules-14341.txt","grammar-2NTs-9rules-26945.txt","grammar-2NTs-5rules-2596.txt","grammar-2NTs-7rules-12007.txt","grammar-2NTs-7rules-12385.txt","grammar-2NTs-7rules-11951.txt",
                "grammar-2NTs-7rules-11489.txt","grammar-2NTs-7rules-11545.txt","grammar-2NTs-7rules-11195.txt","grammar-2NTs-8rules-21294.txt","grammar-2NTs-6rules-652.txt","grammar-2NTs-6rules-6532.txt",
                "grammar-2NTs-8rules-20420.txt","grammar-2NTs-8rules-20882.txt","grammar-2NTs-7rules-13372.txt","grammar-2NTs-7rules-13834.txt",
                "grammar-2NTs-7rules-12079.txt","grammar-2NTs-7rules-12067.txt","grammar-2NTs-7rules-12515.txt","grammar-2NTs-7rules-12527.txt","grammar-2NTs-7rules-12445.txt","grammar-2NTs-7rules-12457.txt"," grammar-2NTs-7rules-11617.txt","grammar-2NTs-7rules-11605.txt",
                "grammar-2NTs-9rules-26385.txt","grammar-2NTs-9rules-26715.txt","grammar-2NTs-7rules-12592.txt","grammar-2NTs-6rules-5892.txt","grammar-2NTs-6rules-6222.txt","grammar-2NTs-6rules-6614.txt","grammar-2NTs-8rules-21297.txt",
                "grammar-2NTs-9rules-26374.txt","grammar-2NTs-9rules-26704.txt","grammar-2NTs-8rules-20410.txt","grammar-2NTs-8rules-20872.txt","grammar-2NTs-6rules-5808.txt","grammar-2NTs-6rules-6138.txt","grammar-2NTs-6rules-6432.txt",
                "grammar-2NTs-6rules-6488.txt","grammar-2NTs-5rules-3328.txt","grammar-2NTs-8rules-18079.txt","grammar-2NTs-8rules-18919.txt","grammar-2NTs-8rules-18023.txt","grammar-2NTs-8rules-18457.txt","grammar-2NTs-8rules-20875.txt",
                "grammar-2NTs-8rules-20413.txt","grammar-2NTs-6rules-6565.txt","grammar-2NTs-8rules-19061.txt","grammar-2NTs-8rules-19385.txt","grammar-2NTs-8rules-18991.txt",
                "grammar-2NTs-8rules-18979.txt","grammar-2NTs-8rules-18139.txt","grammar-2NTs-8rules-19397.txt","grammar-2NTs-8rules-18529.txt","grammar-2NTs-8rules-18517.txt"," grammar-2NTs-8rules-18587.txt","grammar-2NTs-8rules-18151.txt",
                "grammar-2NTs-8rules-19049.txt","grammar-2NTs-8rules-18599.txt","grammar-2NTs-10rules-30322.txt","grammar-2NTs-10rules-30157.txt","grammar-2NTs-7rules-12586.txt","grammar-2NTs-7rules-12589.txt","grammar-2NTs-7rules-12596.txt",
                "grammar-2NTs-9rules-26706.txt","grammar-2NTs-9rules-26376.txt","grammar-2NTs-9rules-25964.txt","grammar-2NTs-8rules-18664.txt","grammar-2NTs-8rules-19126.txt","grammar-2NTs-8rules-19518.txt","grammar-2NTs-8rules-19462.txt","grammar-2NTs-8rules-19906.txt","grammar-2NTs-10rules-29927.txt","grammar-2NTs-9rules-24430.txt"};

        String[] list_from_sourcefile = source.list();

        for (int i=0; i<files.length;i++){
            for(int j=0; j<list_from_sourcefile.length; j++){
                if(files[i].equals(list_from_sourcefile[j])){
                    File sourceFile = new File("C:/Users/evita/Documents/GitHub/compressed-rna/grammars/grammars_first_filter/"+list_from_sourcefile[j]);
                    File dest = new File( "C:/Users/evita/Documents/GitHub/compressed-rna/grammars/100_Best_grammars_Geomean_Adaptive_SemiAdaptive/"+files[i]);
                    //copy file conventional way using Stream
                    //long start = System.nanoTime();
                    copyFileUsingChannel(sourceFile, dest);
                }
            }

        }



    }
    private static void copyFileUsingChannel(File source, File dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }finally{
            sourceChannel.close();
            destChannel.close();
        }
    }
}
