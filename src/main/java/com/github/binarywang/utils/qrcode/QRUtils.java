package com.github.binarywang.utils.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class QRUtils {
    //模板的位置
    private static String templatePath = "";

    private static String baseUrl = System.getProperty("user.dir") + "\\conf\\";
    private static String qrValueTtfFilePath = baseUrl + "arial.ttf";
    private static String usernameTtfFilePath = baseUrl + "arial.ttf";
    private static String passwordTtfFilePath = baseUrl + "arial.ttf";
    private static String txtFilePath = "data.txt";

    //二维码的长和宽
    static int qrcodeWidth = 50;

    private static String splitValue = ">>>>>>";

    private static int usernamefontSize = 9;

    private static int passwordfontSize = 7;

    private static int usernameFontRed= 0;
    private static int usernameFontGreen= 0;
    private static int usernameFontBlue=0;
    private static int usernameX=0;
    private static int usernameY=0;
    private static int  pswFontRed=0;
    private static int pswFontGreen=0;
    private static int pswFontBlue=0;
    private static int pswX=0;
    private static int pswY=0;
    private static int qrcodeX=338;
    private static int qrcodeY=120;


    private static void init(Properties props){
        templatePath = baseUrl + props.getProperty("templateFilePath");
        qrValueTtfFilePath = baseUrl + props.getProperty("qrValueTtfFilePath");
        usernameTtfFilePath = baseUrl + props.getProperty("usernameTtfFilePath");
        passwordTtfFilePath = baseUrl  + props.getProperty("passwordTtfFilePath");
        usernamefontSize = new Integer(props.getProperty("usernamefontSize")).intValue();
        passwordfontSize = new Integer(props.getProperty("passwordfontSize")).intValue();

        usernameFontRed= new Integer(props.getProperty("usernameFontRed")).intValue();
        usernameFontGreen=new Integer(props.getProperty("usernameFontGreen")).intValue();
        usernameFontBlue=new Integer(props.getProperty("usernameFontBlue")).intValue();
        usernameX=new Integer(props.getProperty("usernameX")).intValue();
        usernameY=new Integer(props.getProperty("usernameY")).intValue();
        pswFontRed=new Integer(props.getProperty("pswFontRed")).intValue();
        pswFontGreen=new Integer(props.getProperty("pswFontGreen")).intValue();
        pswFontBlue=new Integer(props.getProperty("pswFontBlue")).intValue();
        pswX=new Integer(props.getProperty("pswX")).intValue();
        pswY=new Integer(props.getProperty("pswY")).intValue();

        qrcodeX=new Integer(props.getProperty("qrcodeX")).intValue();
        qrcodeY=new Integer(props.getProperty("qrcodeY")).intValue();

        splitValue = props.getProperty("splitValue");

        qrcodeWidth=new Integer(props.getProperty("qrcodeWidth")).intValue();
    }
    public static void main(String[] args) {
        Properties props = new Properties();
        String propertiesPath = System.getProperty("user.dir") + "\\conf\\config.properties";
        try{
            InputStream in = new BufferedInputStream(new FileInputStream(propertiesPath));
            props.load(in);
            in.close();
            init(props);
        } catch (Exception e){
            System.out.println(propertiesPath + "文件不存在！");
            e.printStackTrace();
        }
        File directory = new File(new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
        if(!directory.exists()){
            directory.mkdirs();
        }
        List<String> errorList = new ArrayList<String>();
        List<String> lines = readTxtFileIntoStringArrList(System.getProperty("user.dir") + "\\"+ txtFilePath);
        for(String line : lines){
            if(line.contains("二维码")) {
                continue;
            }
            String[] lineArr = line.split(splitValue);
            if(lineArr.length < 3){
                errorList.add(line);
            }
            String qrcodeData = lineArr[0];
            String userName = lineArr[1];
            String password = lineArr[2];
            if(org.apache.commons.lang.StringUtils.isNotBlank(qrcodeData)
                    && org.apache.commons.lang.StringUtils.isNotBlank(userName) && org.apache.commons.lang.StringUtils.isNotBlank(password)){
                try{
                    genPic(directory,qrcodeData,userName,password);
                } catch (Exception e) {
                    errorList.add(line);
                    e.printStackTrace();
                }
            }
        }
        if(errorList.size() > 0){
            System.out.println("--------------------生成失败，请重新生成!----------------------");
            for(String errLine : errorList){
                System.out.println(errLine);
            }
            System.out.println("--------------------生成失败，请重新生成!----------------------");
        } else {
            System.out.println("生成成功!");
        }
    }
    public static void genPic(File directory,String qrcodeData,String userName,String password) throws IOException, DocumentException, WriterException {
        //加载自定义的字体，ttf格式
        InputStream inputStream = new FileInputStream(new File(templatePath));
        PdfReader reader = new PdfReader(inputStream);
        //如果不保存到文件的话可以用
        String outPdfPath = directory.getAbsolutePath() + "\\" + userName + "#" + password + ".pdf";
        OutputStream outputStream = new FileOutputStream(new File(outPdfPath));
        PdfStamper stamper = new PdfStamper(reader, outputStream);

        PdfContentByte page = stamper.getOverContent(1);
        page.beginText();
        page.setFontAndSize(BaseFont.createFont(usernameTtfFilePath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED), usernamefontSize);
        page.setColorFill(new BaseColor(usernameFontRed, usernameFontGreen, usernameFontBlue));
        page.setTextMatrix(usernameX,usernameY);
        page.showText(userName);
        page.endText();

        page.beginText();
        page.setFontAndSize(BaseFont.createFont(passwordTtfFilePath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED), passwordfontSize);
        page.setColorFill(new BaseColor(pswFontRed, pswFontGreen, pswFontBlue));
        page.setTextMatrix(pswX,pswY);
        page.showText(password);
        page.endText();


        // 画中间的二维码
        Map<EncodeHintType, Object> config = new HashMap<EncodeHintType, Object>();
        config.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        config.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        config.put(EncodeHintType.MARGIN, 1);
        MatrixToImageConfig con = new MatrixToImageConfig(Color.BLACK.getRGB(), Color.WHITE.getRGB());
        //此处二维码的长宽都是900像素，尽可能的让长宽大一些，然后调 image.scaleToFit(qrcodeWidth,qrcodeWidth);如果长宽太小的话 二维码会拉
        //伸后画上去，你会发现二维码背后有噪点，图像放大失真导致的，如果你画的二维码大一些，就不会存在放大失真的问题。
        BitMatrix bitMatrix = new MultiFormatWriter().encode(qrcodeData, BarcodeFormat.QR_CODE, 900, 900, config);
        BufferedImage codeImage = MatrixToImageWriter.toBufferedImage(bitMatrix, con);
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        ImageIO.write(codeImage, "jpg", fos);
        Image image = Image.getInstance(fos.toByteArray());
        image.scaleToFit(qrcodeWidth, qrcodeWidth);
        //设置图片在页面中的坐标
        image.setAbsolutePosition(qrcodeX,qrcodeY);
        page.addImage(image);
        stamper.close();
        reader.close();
        inputStream.close();
    }

    //如果显示的文字超长的话，字体相应的要缩小，直至能显示一行为止
    private static int getMinFontSize(BaseFont font, int fontSize, float maxLength, String content) {
        float length = font.getWidthPoint(content, fontSize);
        while (length > maxLength) {
            fontSize--;
            length = font.getWidthPoint(content, fontSize);
        }
        return fontSize;
    }
    /**
     * 功能：Java读取txt文件的内容 步骤：1：先获得文件句柄 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流 4：一行一行的输出。readline()。 备注：需要考虑的是异常情况
     *
     * @param filePath
     *            文件路径[到达文件:如： D:\aa.txt]
     * @return 将这个文件按照每一行切割成数组存放到list中。
     */
    public static List<String> readTxtFileIntoStringArrList(String filePath)
    {
        List<String> list = new ArrayList<String>();
        try
        {
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists())
            { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;

                while ((lineTxt = bufferedReader.readLine()) != null)
                {
                    list.add(lineTxt);
                }
                bufferedReader.close();
                read.close();
            }
            else
            {
                System.out.println("找不到指定的文件");
            }
        }
        catch (Exception e)
        {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return list;
    }
}
