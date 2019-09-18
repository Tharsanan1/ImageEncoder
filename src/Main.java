import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
/*
    how to generate a token in small lenght
    use ascii reporesentation 
    we cannot use del enter ascii char to represent the token so we can only use ascii between 
    33 and 126
    so the strategy is to represent a numer using ascii addition 
    ! (33) will represent 0 
    ~ (126) will represent 93
    000 will be represented by !      (33-33 = 0)
    001 will be represented by "      (34-33 = 1)
    ....
    092 will be represented by }      (125-33 = 92)
    093 will be represented by ~!     (126+33 - 33 - 33 = 93)
    094 will be represented by ~"     (126+34 - 33 - 33 = 94)
    ....
    186 will be represented by ~~!    (126+126+33 - 33 - 33 -33 = 186)
    187 will be represented by ~~"    (126+126+34 - 33 - 33 -33 = 187)
    ....
    255 will be represented by ~~f    (126+126+102 - 33 - 33 -33 = 187)

*/




public class Main {
    static ArrayList<String> path = new ArrayList<>();
    public static void main(String[] args) {        
        JFrame frame = new JFrame("JFrame Image Encoder");
        frame.setResizable(false);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        JLabel label = new JLabel("<html><br><br><br>Paste your token here (length must be > 7 ): </html>");
        JButton buttonEncode = new JButton();
        JTextField textField = new JTextField("",40);
        JCheckBox exchangingCard1 = new JCheckBox("Check this box for higher security");
        panel.add(exchangingCard1);
        exchangingCard1.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){

                }
            }
        });
        buttonEncode.setText("Encode");
        buttonEncode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(exchangingCard1.isSelected()){
                    // Should develop rotation encoding functionality init code here
                }
                else{
                    if(textField.getText().length()>7){
                        if(validateToken(textField.getText())) {
                            chooseFile();
                            for (String pathToFIle : path) {
                                encodeImage(pathToFIle, textField.getText());
                            }
                            path.clear();
                        }
                    }
                }
            }
        });
        JButton buttonDecode = new JButton("Decode");
        buttonDecode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(exchangingCard1.isSelected()){
                    // Should develop rotation encoding functionality init code here
                }
                else {
                    if (textField.getText().length() > 7) {
                        if (validateToken(textField.getText())) {
                            chooseFile();
                            for (String pathToFIle : path) {
                                decodeImage(pathToFIle, textField.getText());
                            }
                            path.clear();
                        }
                    }
                }
            }
        });
        JButton buttonTokenGen = new JButton("Generate token");
        JLabel labelToken = new JLabel("      Generated Token :     ");
        JTextArea textfieldTokenGen = new JTextArea("");
        buttonTokenGen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = createSmallTokenString(getpixelencodeValues());
                textfieldTokenGen.setText(text);
            }
        });
        JButton buttonCopyLast = new JButton("CopyLastGeneratedCode");
        buttonCopyLast.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringSelection stringSelection = new StringSelection(textfieldTokenGen.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            }
        });
        //panel.add(label);
        JLabel labelForInstruction = new JLabel("<html>Step 1: If you have A token You can paste that on the token <br>" +
            " area and encode your image files with encode button.<br><br>Step 2: If you want to decode an image," +
            " you need to paste<br> the currect token, which you used to encode the same image.<br><br>" +
            "Step 3: If you need new token for encoding simply <br>" +
            "click generate btn and copy token from generated token area.<br><br>" +
            "Note: If you loose your token, there is no way to recover your <br> " +
            "original image.</html>");
        panel.add(label);
        panel.add(textField);
        panel.add(buttonEncode);
        panel.add(buttonDecode);
        panel.add(buttonTokenGen);
        panel.add(labelToken);
        panel.add(buttonCopyLast);
        //panel.add(textfieldTokenGen);
        panel.add(labelForInstruction);
        frame.add(panel);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        // the following functionalities are under development.
        labelForInstruction.setVisible(false);
        buttonCopyLast.setVisible(false);
        buttonTokenGen.setVisible(false);
        labelToken.setVisible(false);
        exchangingCard1.setVisible(false);
        Random rand = new Random();

        

    }
    public static int getEncodedValue(ArrayList<Integer> keyList, int value, int i, int j){
        return getEncodedValueUsingRotateList(keyList, value, i,  j);
    }

    public static int getDecodedValue(ArrayList<Integer> keyList, int value, int i, int j){
        return getDecodedValueUsingRotateList(keyList, value, i,  j);
    }

    public static int getEncodedValueUsingRotateList(ArrayList<Integer> keyList, int value, int i, int j){
        int count = i+j;
        int toOut = keyList.get((value+count)%256);
        return toOut;

    }

    // toOut = ( value + (count%256) ) % 256    if toOut grater
    // than or equal to (count%256) it means the second modulo
    // 256 doesnt affect any result so toOut = value + (count%256)
    // so value = toOut-count%256
    // but if toOut < count%256 it means second modulo 256 has affected
    // toOut so we need to consider second modulo also. in that case
    // toOut + 256 = value + (count%256)
    // value = toOut + 256 - (count%256)
    public static int getDecodedValueUsingRotateList(ArrayList<Integer> keyList, int value, int i, int j){
        int count = i+j;
        int toOut = keyList.indexOf(value);
        if(toOut>=count%256){
            return toOut-count%256;
        }
        else{
            return 256-(count%256 - toOut);
        }

    }
    public static void encodeImage(BufferedImage img, ArrayList<Integer> pixelEncodeValue){
        int width = img.getWidth();
        int height = img.getHeight();

        for(int i = 0; i<width; i++){
            for(int j = 0; j<height; j++){
                int p = img.getRGB(i,j);
                //System.out.println(width+" "+ height+" "+p);

                int a = (p>>24) & 0xFF;
                int r = (p>>16) & 0xFF;
                int g = (p>>8) & 0xFF;
                int b = (p) & 0xFF;
                //if(255-r > 60)
                //System.out.print("a: "+a+" r: "+r+" g: "+g+" b: "+b+"");
                //set the pixel value
                p = a<<24 | (getEncodedValue(pixelEncodeValue,r,i,j)<<16) | (getEncodedValue(pixelEncodeValue,g,i+1,j)<<8) | getEncodedValue(pixelEncodeValue,b,i+2,j);
                img.setRGB(i, j, p);
            }
        }
    }
    public static void decodeImage(BufferedImage imageDecoded, ArrayList<Integer> pixelEncodeValue){
        int widthToDecode = imageDecoded.getWidth();
        int heightToDecode = imageDecoded.getHeight();

        for(int i = 0; i<widthToDecode; i++){
            for(int j = 0; j<heightToDecode; j++){
                int p = imageDecoded.getRGB(i,j);

                int a = (p>>24) & 0xFF;
                int r = (p>>16) & 0xFF;
                int g = (p>>8) & 0xFF;
                int b = (p) & 0xFF;
                p = a<<24 | (getDecodedValue(pixelEncodeValue,r,i,j)<<16) | (getDecodedValue(pixelEncodeValue,g,i+1,j)<<8) | getDecodedValue(pixelEncodeValue,b,i+2,j);
                imageDecoded.setRGB(i, j, p);

            }
        }
    }

    public static ArrayList<Integer> getpixelencodeValues(){
        Random rand = new Random();
        ArrayList<Integer> pixelValue = new ArrayList<>();
        ArrayList<Integer> pixelEncodeValue = new ArrayList<>();
        for(int i = 0; i < 256; i++){
            pixelValue.add(i);
        }
        for(int i = 0; i<256; i++){
            int  n = rand.nextInt(256-i);
            pixelEncodeValue.add(pixelValue.get(n));
            pixelValue.remove(n);
        }
        return pixelEncodeValue;
    }


    public static void encodeImage(String path, String token){
        BufferedImage img = null;
        File f = null;
        ArrayList<Integer> pixelEncodeValue = createRandomOrderPixelFromSeed(token);

        try{
            f = new File(path);
            img = ImageIO.read(f);
        }catch(IOException e){
            System.out.println(e);
        }

        encodeImage(img,pixelEncodeValue);
        Collections.reverse(pixelEncodeValue);
        encodeImage(img,pixelEncodeValue);
        try{
            f.delete();
            File file = new File(path.substring(0,path.lastIndexOf(".")+1)+"png");
            ImageIO.write(img, "png", file);

        }catch(IOException e){
            System.out.println(e);
        }
    }
    public static void decodeImage(String path, String token){
        File f = null;
        ArrayList<Integer> pixelEncodeValue = createRandomOrderPixelFromSeed(token);
        BufferedImage imageDecoded = null;
        try {
            imageDecoded = ImageIO.read(new File(path));
            Collections.reverse(pixelEncodeValue);
            decodeImage(imageDecoded,pixelEncodeValue);
            Collections.reverse(pixelEncodeValue);
            decodeImage(imageDecoded,pixelEncodeValue);


            f = new File(path);
            ImageIO.write(imageDecoded, "png", f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void chooseFile(){
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setMultiSelectionEnabled(true);

        int returnValue = jfc.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            File[] selectedFiles = jfc.getSelectedFiles();
            for (File f: selectedFiles ) {
                path.add(f.getAbsolutePath());
            }

        }
    }

    public static String createSmallTokenString(ArrayList<Integer> pixelEncodeValue){

        String token = "";
        for(int i = 0; i < pixelEncodeValue.size(); i++){
            if(pixelEncodeValue.get(i) < 93){
                token += (char) (pixelEncodeValue.get(i) + 33);
            }
            else if (pixelEncodeValue.get(i) > 92 && pixelEncodeValue.get(i) < 186){
                int excess = pixelEncodeValue.get(i) - 93;
                token += (char) 126;
                token += (char)(excess + 33);
            }
            else{
                int excess = pixelEncodeValue.get(i) - 93 - 93;
                token += (char) 126;
                token += (char) 126;
                token += (char)(excess + 33);
            }
        }
        return token;
    }

    public static  ArrayList<Integer> getEncodeIntegersArrayList(String key){
        ArrayList<Integer> intArr = new ArrayList<Integer>();
        int len = key.length();
        int pointer = 0;
        while(true){
            int p = pointer;
            if(key.charAt(pointer) == '~'){
                if(pointer < (len - 1)){
                    if(key.charAt(pointer+1) == '~'){
                        int c = key.charAt(pointer + 2);
                        intArr.add((126 + 126 + c - 33 - 33 -33));
                        pointer = pointer + 3;
                    }
                    else{
                        int c = key.charAt(pointer + 1);
                        intArr.add((126 + c - 33 - 33));
                        pointer = pointer + 2; 
                    }
                }
                else{
                    throw new java.lang.Error("this is very bad");
                }
            }
            else{
                int c = key.charAt(pointer);
                intArr.add(( c - 33));
                pointer = pointer + 1; 
            }
            if(pointer >= len){
                break;
            }
        }

        return intArr;
    }

    public static boolean validateToken(String token){
        boolean flag = true;
        if(token.length()>7){
            return true;
        }
        try {
            ArrayList<Integer> numList = getEncodeIntegersArrayList(token);
            if(numList.size() != 256){
                return false;
            }
            HashSet<Integer> foundNumbers = new HashSet<Integer>();
            for (int num : numList) {
                if(foundNumbers.contains(num)){
                    return false;
                }
                foundNumbers.add(num);
            }
        }
        catch (Exception e){
            return false;
        }
        return flag;
    }

    public static ArrayList<Integer> createRandomOrderPixelFromSeed(String key){
        int seed = key.hashCode();
        Random rand = new Random(seed);
        ArrayList<Integer> pixelValue = new ArrayList<>();
        ArrayList<Integer> pixelEncodeValue = new ArrayList<>();
        for(int i = 0; i < 256; i++){
            pixelValue.add(i);
        }
        for(int i = 0; i<256; i++){
            int  n = rand.nextInt(256-i);
            pixelEncodeValue.add(pixelValue.get(n));
            pixelValue.remove(n);
        }
        return pixelEncodeValue;
    }
}
/*
    000 will be represented by !      (33-33 = 0)
    001 will be represented by "      (34-33 = 1)
    ....
    092 will be represented by }      (125-33 = 92)
    093 will be represented by ~!     (126+33 - 33 - 33 = 93)
    094 will be represented by ~"     (126+34 - 33 - 33 = 94)
    ....
    186 will be represented by ~~!    (126+126+33 - 33 - 33 -33 = 186)
    187 will be represented by ~~"    (126+126+34 - 33 - 33 -33 = 187)
    ....
    255 will be represented by ~~f    (126+126+102 - 33 - 33 -33 = 187)

*/