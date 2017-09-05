
package GalleryGenerator;

import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import com.sun.imageio.plugins.jpeg.JPEGImageReader;
import com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi;
import com.sun.imageio.plugins.jpeg.JPEGImageWriter;
import com.sun.imageio.plugins.jpeg.JPEGImageWriterSpi;
import com.sun.imageio.plugins.png.PNGImageReader;
import com.sun.imageio.plugins.png.PNGImageReaderSpi;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.JProgressBar;

public class ImageProcessor {
    private class ImageParams {
        public int     previewWidth    = 0; 
        public int     previewHeight   = 0;
        public int     originalWidth   = 0; 
        public int     originalHeight  = 0;
        public double  outputResize    = 1.19;
        public String  originalName    = "";
        public String  convertedName   = "";
        public String  previewName     = "";
        public String  logoPath        = "./logo.png";
        public String  textPath        = "./copypaste.txt";
        public boolean isLogo          = false;
        public boolean retError        = false;
        
        public ImageParams() {}
    }
    
    private class NameFilter {
        int action_type;
        String s_from;
        String s_to;
        NameFilter () { }
    }
    
    private class Converter {
        ImageParams lip = new ImageParams();
        ImageParams cip = new ImageParams();
    
        public Converter() { }
        
        public Converter(ImageParams ip) { 
            cip.previewWidth  = ip.previewWidth;
            cip.previewHeight = ip.previewHeight;
            cip.outputResize  = ip.outputResize;
            cip.previewName   = ip.previewName;
            cip.convertedName = ip.convertedName;
            cip.logoPath      = ip.logoPath;
            cip.originalName  = ip.originalName;
        }
        
    
        public void setPreviewParam(int PreviewWidth, int PreviewHeight) {
            cip.previewWidth  = PreviewWidth;
            cip.previewHeight = PreviewHeight;
        }

        public void setOutputResizeValue(double OutputResize) {
            if ((OutputResize > 10) || (OutputResize < 0.2)) {
                cip.outputResize = 1;
                return;
            }
            cip.outputResize = OutputResize;
        }

        private boolean setPath(String path) {
            File f = new File(path);
            if (f.isFile() || f.isDirectory()) {
                return false;
            } else {
                if (new File(f.getParent()).isDirectory()) {

                    return true;
                } else {
                    return false;
                }
            }
        }
        
        private boolean isFile(String path) {
            File f = new File(path);
            if (f.isFile() && f.canRead()) {
                return true;
            } else {
                return false;
            }
        }

        public boolean setOriginalPath(String path) {
            if (isFile(path)) {
                cip.originalName = path;
                return true;
            } else {
                return false;
            }
        }
        
        public boolean setOutputPath(String path) {
            if (setPath(path)) {
                cip.convertedName = path;
                return true;
            } else {
                return false;
            }
        }

        public boolean setPreviewPath(String path) {
            if (setPath(path)) {
                cip.previewName = path;
                return true;
            } else {
                return false;
            }
        }
        
        public boolean isConverterReady() {
            if ((setPath(cip.previewName)) && (setPath(cip.convertedName)) && (isFile(cip.originalName)) && 
                    (cip.outputResize < 10) && (cip.outputResize > 0.2) && (cip.previewWidth > 40) && (cip.previewHeight > 40)) {
                return true;
            } else {
                return false;
            }
        }

        private int imageProcess(String original_path, String convertedPath, double convertedResize, String previewPath, int PreviewW, int PreviewH) {
            lip.previewWidth  = PreviewW;
            lip.previewHeight = PreviewH;
            lip.originalName  = original_path;
            lip.previewName   = previewPath;
            lip.convertedName = convertedPath;
            try {
                byte [] header = new byte[10];
                FileInputStream fis = new FileInputStream(original_path);
                DataInputStream dis = new DataInputStream(fis);
                dis.read(header);

                String h = new String(header).trim();
                ImageReader image;
    //            String file_extention = "";
                if(h.contains("PNG")) {
                    image = new PNGImageReader(new PNGImageReaderSpi());
    //                file_extention = ".png";
                } else if(h.contains("GIF89")) {
                    image = new GIFImageReader(new GIFImageReaderSpi());
    //                file_extention = ".gif";
                } else if(h.contains("JFIF")){
                    image = new JPEGImageReader(new JPEGImageReaderSpi());
    //                file_extention = ".jpg";
                } else {
                    cip.retError = true;
                    lip.retError = true;
                    System.out.println("ERROR - Not an image: " + original_path);
                    return -1;
                }

                dis.close();
                fis.close();

                FileImageInputStream fiis = new FileImageInputStream(new File(original_path));
                image.setInput(fiis);
                ImageReadParam param = new ImageReadParam();
                BufferedImage img = image.read(0, param);

                /*  PREVIEW BLOCK  */
                BufferedImage pImage = new BufferedImage(PreviewW, PreviewH, img.getType());
                Graphics pimgg = pImage.getGraphics();

                double sourse_img_w = img.getWidth(), sourse_img_h = img.getHeight(), up_val = 0, rect_xy = 0;
                if ((sourse_img_w < PreviewW) || (sourse_img_h < PreviewH)) {
                    pImage.flush();
                    pimgg.dispose();
                    img.flush();
                    fiis.close();
                    image.dispose();
                    cip.retError = true;
                    lip.retError = true;
                    return -3;
                }
                
                lip.originalWidth = (int)sourse_img_w;
                lip.originalHeight = (int)sourse_img_h;
                
                double rect_x0 = 0, rect_y0 = 0, rect_x1 = 0, rect_y1 = 0, corr_value1 = 0;
                if (sourse_img_w > sourse_img_h) {
                    rect_y0 = 0;
                    rect_y1 = sourse_img_h;

                    up_val  = sourse_img_h / PreviewH;
                    rect_xy = PreviewW * up_val;

                    corr_value1 = (sourse_img_w - rect_xy) / 2;

                    rect_x0 = corr_value1;
                    rect_x1 = sourse_img_w - corr_value1;
                } else if (sourse_img_w < sourse_img_h) {
                    rect_x0 = 0;
                    rect_x1 = sourse_img_w;

                    up_val = sourse_img_w / PreviewW;
                    rect_xy = PreviewH * up_val;

                    corr_value1 = (sourse_img_h - rect_xy) / 2;

                    rect_y0 = corr_value1;
                    rect_y1 = sourse_img_h - corr_value1;
                } else {
                    rect_x0 = 0;
                    rect_y0 = 0;
                    rect_x1 = sourse_img_w;
                    rect_y1 = sourse_img_h;
                }

                pimgg.drawImage(img, 0, 0, PreviewW, PreviewH, (int)rect_x0,(int)rect_y0, (int)rect_x1, (int)rect_y1, null);

                ImageWriter iw = new JPEGImageWriter(new JPEGImageWriterSpi());
                FileImageOutputStream fois = new FileImageOutputStream(new File(previewPath));
                iw.setOutput(fois);
                iw.write(pImage);
                fois.close();
                iw.dispose();

                pimgg.dispose();
                pImage.flush();

                /*  MAIN BLOCK  */

                double new_w, new_h;
                new_w = sourse_img_w * convertedResize;
                new_h = sourse_img_h * convertedResize;

                BufferedImage nImage = new BufferedImage((int)new_w, (int)new_h, img.getType());
                Graphics nimgg = nImage.getGraphics();

                nimgg.drawImage(img, 0, 0, (int)new_w, (int)new_h, 0, 0, (int)sourse_img_w, (int)sourse_img_h, null);

                if (new File(cip.logoPath).canRead()) {
                    ImageReader ir_logo = new PNGImageReader(new PNGImageReaderSpi()); 
                    FileImageInputStream fiis_logo = new FileImageInputStream(new File(cip.logoPath));
                    ir_logo.setInput(fiis_logo);
                    ImageReadParam param_logo = new ImageReadParam();
                    BufferedImage img_logo = ir_logo.read(0, param_logo);

                    nimgg.drawImage(img_logo, (int)new_w - img_logo.getTileWidth(), (int)new_h - img_logo.getHeight(), (int)new_w, (int)new_h, 0, 0, img_logo.getWidth(), img_logo.getHeight(), null);

                    img_logo.flush();
                    fiis_logo.close();
                    ir_logo.dispose();
                    lip.isLogo = true;
                } else {
                    lip.isLogo = false;
                }

                ImageWriter niw = new JPEGImageWriter(new JPEGImageWriterSpi());
                FileImageOutputStream nfois = new FileImageOutputStream(new File(convertedPath));
                niw.setOutput(nfois);
                niw.write(nImage);
                nfois.close();
                niw.dispose();

                pimgg.dispose();
                pImage.flush();

                img.flush();
                fiis.close();
                image.dispose();
                cip.retError = false;
                lip.retError = false;
                return 0;
            } catch (IOException ex) {
                System.out.println("ERROR - Unknown converter error");
                cip.retError = true;
                lip.retError = true;
                return -2;
            }       
        }

        public void convertImage(String image_path) {
            imageProcess(image_path, cip.convertedName, cip.outputResize, cip.previewName, cip.previewWidth, cip.previewHeight);
        }
        
        public ImageParams getLastImageParams() {
            return lip;
        }
    }
    
    public class SQLMaker {
        private String  sql_buffer  = "";
        private String  sql_begin   = "";
        
        private RandomAccessFile RAF = null;
        
        public SQLMaker(String tableName) {
            sql_begin = "INSERT INTO " + tableName + " VALUES";
        }
        
        public void writeToSQL(String queryPart) {
            if (sql_buffer.length() < 2) {
                sql_buffer = sql_buffer + sql_begin;
            }
            sql_buffer = sql_buffer + "(" + queryPart + "),";
            if (sql_buffer.length() > 10000) {
                syncSQLFile();
            }
        }
        
        public boolean syncSQLFile() {
            try {
                if (sql_buffer.endsWith(",")) {
                    sql_buffer = sql_buffer.substring(0, sql_buffer.length() - 1);
                }
                sql_buffer = sql_buffer + ";\r\n";
                
                if (RAF.length() > 0) { 
                    RAF.seek(RAF.length()); 
                }
                RAF.write(sql_buffer.getBytes());
                sql_buffer = "";
                return true;
            } catch (IOException ex) {
                return false;
            } 
        }
        
        public void closeSLQFile() {
            try {
                RAF.close();
            } catch (IOException ex) { }
        }
        
        public boolean openSQLFile(String path) {
            try {
                RAF = new RandomAccessFile(path, "rw");
                return true;
            } catch (FileNotFoundException ex) { 
                return false;
            } catch (IOException ex) { 
                return false;
            }
        }
    }
    
    private SQLMaker sqlGalleries = new SQLMaker("galleries");
    private SQLMaker sqlImages    = new SQLMaker("images");
    
    private ImageParams lastImageData   = new ImageParams();
    private ImageParams cip;
    
    private ArrayList<NameFilter> nameFilter = new ArrayList<NameFilter>();
    private ArrayList<String>Directories = new ArrayList<String>();
    
    private Converter conv;
    private LibTools  lt = new LibTools();
    
    private JProgressBar callback_jpb_dir = null;
    private JProgressBar callback_jpb_all = null;
    
    private int         dirCounter              = 0;
    private boolean     isSTOP                  = false;
    
    private int         SQL_images_max_days     = 90;
    private int         PreviewHeight           = 120;
    private int         PreviewWidth            = 120;
    private int         corrIDValue             = 0;
    private int         textCounter             = 0;
    private int         tagsCounter             = 0;
    private double      Resize                  = 1.17;
    private String      LogoPath                = "";
    private String      AllTags[];
    private String      GalleryRoot             = "";
    private String      GalleryNewRoot          = "";
    private String      FilterPath              = "";
    private String      TextPath                = "";
    private boolean     useLOGO                 = false;
    private boolean     useFilter               = false;
    
    public ImageProcessor() {}

    private String generateDate() {
        Calendar c = GregorianCalendar.getInstance();
        c.setTime(new Date());
        Random r = new Random();
        c.add(Calendar.DAY_OF_MONTH, r.nextInt(SQL_images_max_days));
        c.add(Calendar.HOUR_OF_DAY, r.nextInt(23));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
        return formatter.format(c.getTime());
    }
    
    private String tagGen() {
        tagsCounter++;
        if (tagsCounter>=AllTags.length) { tagsCounter=0; }
        return AllTags[tagsCounter];
    }
    
    private int getDirsCount() {
        if (callback_jpb_all != null) {
            callback_jpb_all.setMaximum(100);
            callback_jpb_all.setMinimum(0);
            callback_jpb_all.setStringPainted(true); 
            callback_jpb_all.setValue(0);
            callback_jpb_all.setString("");
        }
        
        FilenameFilter ff = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name)   {
                return new File(dir, name).isDirectory();
            };
        };
        
        File f = new File(GalleryRoot);
        String fl[] = f.list(ff);
        for (int i=0; i<fl.length; i++) {
            Directories.add(fl[i]); 
            if (callback_jpb_all != null) {
                callback_jpb_all.setString("Calculate directories [" + dirCounter + "]");
            }
            dirCounter++;
        }
        
        if (callback_jpb_all != null) {
            callback_jpb_all.setValue(0);
            callback_jpb_all.setString("");
        }
        return dirCounter;
    }

    private String applyFilters(String name) {
        if (useFilter == false) { return name; }
        
        NameFilter NF;
        for (int i=0; i<nameFilter.size(); i++) {
            NF = nameFilter.get(i);
            switch (NF.action_type) {
                case 1:
                    name = name.trim().replace(NF.s_from, "");
                    break;
                case 2:
                    name = name.trim().replace(NF.s_from, NF.s_to);
                    break;
                case 3:
                    name = NF.s_from + name.trim();
                    break;
                case 4: 
                    name =  name.trim() + NF.s_from;
                    break;
                case 5:
                    name = name.trim().replace("/ ", "/").replace(" /", "/").replace("\\ ", "\\").replace(" \\", "\\");
                    break;
                case 6:
                    name = name.trim().replaceAll(NF.s_from, NF.s_to);
                    break;
                case 0:
                    break;
            }
        }
        return name;
    }
    
    private void filesList(File file, int galID, String newFolderName) {
        int     listCount       = 0;
        int     goodCounter     = 0;
        File    newFile;
        String  listOfFiles[]   = file.list();
        String  currentFile     = "";
        String  newImageName    = "";
//        String  imageName       = "";
        
        listCount = listOfFiles.length;
        if (callback_jpb_dir != null) {
            callback_jpb_dir.setMaximum(listCount);
            callback_jpb_dir.setMinimum(0);
            callback_jpb_dir.setStringPainted(true); 
        }
        sqlImages.openSQLFile(lt.textSlash(GalleryNewRoot) + "10_images.sql");
                
        for (int i=0; i<listCount; i++) {
            if (isSTOP) { break; }
            if ((listOfFiles[i].toLowerCase().endsWith(".jpg")) || (listOfFiles[i].toLowerCase().endsWith(".gif")) || (listOfFiles[i].toLowerCase().endsWith(".png"))) {
                currentFile = lt.textSlash(file.getPath()) + listOfFiles[i];
                newFile = new File(currentFile);
                if (newFile.isFile() && newFile.canRead()) {
//                    imageName = deleteLastSlash(newPath);
//                    imageName = getParentFolderName(imageName);
                    
                    String keyWord = tagGen();
                    
                    newImageName = lt.textRusToTranslit(keyWord.replace(" ", "_")) + "-" + new String(i + "00000").substring(0, 3); 
                    cip = new ImageParams();
                    cip.convertedName   = lt.textSlash(lt.textSlash(GalleryNewRoot) + newFolderName) + newImageName + ".jpg";
                    cip.logoPath        = LogoPath;
                    cip.originalName    = currentFile;
                    cip.outputResize    = Resize;
                    cip.previewHeight   = PreviewHeight;
                    cip.previewWidth    = PreviewWidth;
                    cip.previewName     = lt.textSlash(lt.textSlash(GalleryNewRoot) + newFolderName) + newImageName + ".preview.jpg";
                    
                    conv = new Converter(cip);
                    if (conv.isConverterReady()) {
                        conv.convertImage(currentFile);
                        lastImageData = conv.getLastImageParams();
                        if (lastImageData.retError == false) {
                            // id, galid, addedTime, imageName, galName, imageTags, originalWidth, originalHeight, previewWidth, previewHeight, reserved
                            sqlImages.writeToSQL("0, " + galID + ", '" + generateDate() + "', '" + newImageName + "', '" + newFolderName + "', '" + keyWord + "', "
                                    + lastImageData.originalWidth + ", " +  lastImageData.originalHeight + ", " + lastImageData.previewWidth + ", " + lastImageData.previewHeight + ", 0, '" + getTexts() + "'");
                        }
                    }
                    goodCounter++;
                }
            }
            if (callback_jpb_dir != null) {
                callback_jpb_dir.setValue(i);
                callback_jpb_dir.setString(i + " of " + (listCount-1) + "; skipped " + (i - goodCounter)); 
            }
        }
        sqlImages.syncSQLFile();
        sqlImages.closeSLQFile();
        if (callback_jpb_dir != null) {
            callback_jpb_dir.setValue(0);
            callback_jpb_dir.setString("");
        }
    }

    private void ReadDir() {
        int dirCount = Directories.size();
        if (callback_jpb_all != null) {
            callback_jpb_all.setMaximum(dirCount - 1);
            callback_jpb_all.setMinimum(0);
            callback_jpb_all.setStringPainted(true); 
        }
        
        sqlGalleries.openSQLFile(lt.textSlash(GalleryNewRoot) + "20_galleries.sql");
        initTexts();
        
        String oldName, newName, newFileName;
        
        for (int i=0; i<dirCount; i++) {
            if (isSTOP) { break; }
            callback_jpb_all.setString(i + " of " + dirCount); 
            callback_jpb_all.setValue(i); 
            
            oldName = Directories.get(i);
            newName = applyFilters(oldName).replace("'", "").replace("`", "").replace("\"", "");
            newFileName = lt.textRusToTranslit(newName).replaceAll("\\W+", "_").replaceAll("[_]{2,}", "").replaceAll("_$", "").replaceAll("^_", "").toLowerCase();
            
            File f1 = new File(lt.textSlash(GalleryNewRoot) + newFileName);
            f1.mkdir();

            // id, gid, name, fileName, type, reserved
            sqlGalleries.writeToSQL("0, 0, '" + newName + "', '" + newFileName + "', 0, 0");
            
            filesList(new File(lt.textSlash(GalleryRoot) + oldName), i + corrIDValue, newFileName);
            sqlGalleries.syncSQLFile();
        }
        
        sqlGalleries.closeSLQFile();
        closeTexts();
        
        if (callback_jpb_all != null) {
            callback_jpb_all.setValue(0);
            callback_jpb_all.setString("");
        }
    }
    
    private Runnable nThread = new Runnable() {
        @Override
        public void run() {
            getDirsCount();
            ReadDir();
        }
    };
    
    /* =================================================================================================== */
    private FileReader frTexts;
    private BufferedReader brTexts;
    
    private void initTexts() {
        try {
            textCounter = 0;
            frTexts = new FileReader(TextPath);
            brTexts = new BufferedReader(frTexts);
            
            String f = lt.fileReadAll("./settings/copypasteIndex.conf");
            if (f.length() > 3) {
                String g[] = f.split(":");
                String h = lt.textMD5(g[1].trim());
                if (h.contains(lt.textMD5(TextPath))) {
                    textCounter = new Integer(g[0]);
                    for (int i=0; i<textCounter; i++) {
                        try {
                            String a = brTexts.readLine();
                        } catch (IOException ex) {
                            //Logger.getLogger(ImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    lt.fileWriteNew("./settings/copypasteIndex.conf", "0:" + lt.textMD5(TextPath));
                    textCounter = 0;
                }
            } else {
                lt.fileWriteNew("./settings/copypasteIndex.conf", "0:" + lt.textMD5(TextPath));
                textCounter = 0;
            }
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(ImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void closeTexts() {
        try {
            brTexts.close();
            frTexts.close();
        } catch (IOException ex) {
            //Logger.getLogger(ImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String getTexts() {
        String s = "";
        try {
            s = brTexts.readLine();
            if (s==null) {
                return null;
            }
            s = s.replace("'", "").replace("\"", "").replace("`", "").trim();
            textCounter++;
            lt.fileWriteNew("./settings/copypasteIndex.conf", textCounter + ":" + lt.textMD5(TextPath));
        } catch (IOException ex) {
            //Logger.getLogger(ImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            
        }
        return s;
    }
    /* =================================================================================================== */
    
    public boolean setTexts(String path) {
        File f = new File(path);
        if (f.canRead() && f.isFile()) {
            TextPath = path.trim();
            textCounter = 0;
            return true;
        } else {
            return false;
        }
    }
    
    public void Run() {
        isSTOP = false;
        new Thread(nThread).start(); 
    }
    
    public void Stop() {
        isSTOP = true;
    }

    public boolean loadFilterList() {
        if (useFilter == false) { return true; }
        
        String file_content = lt.fileReadAll(FilterPath);
        String file_lines[] = file_content.replace("\r", "").split("\n");
        if (file_lines.length < 2) {
            if (file_lines.length < 2) {
                return false;
            }
        }

        NameFilter tff;
        String spl_arr[];
        for (int i=0; i<file_lines.length; i++) {
            if ((file_lines[i].length() > 0) && (file_lines[i].trim().startsWith("#") == false)) {
                spl_arr = file_lines[i].split(":");
                tff = new NameFilter();
                if (spl_arr.length > 0) {
                    tff.action_type = 0;
                    tff.s_from = "";
                    tff.s_to = "";
                    if (spl_arr[0].equalsIgnoreCase("DEL") == true) { tff.action_type = 1; }
                    if (spl_arr[0].equalsIgnoreCase("REP") == true) { tff.action_type = 2; }
                    if (spl_arr[0].equalsIgnoreCase("INS") == true) { tff.action_type = 3; }
                    if (spl_arr[0].equalsIgnoreCase("INE") == true) { tff.action_type = 4; }
                    if (spl_arr[0].equalsIgnoreCase("TRM") == true) { tff.action_type = 5; }
                    if (spl_arr[0].equalsIgnoreCase("REG") == true) { tff.action_type = 6; }
                }
                if (spl_arr.length > 1) { tff.s_from = spl_arr[1]; }
                if (spl_arr.length > 2) { tff.s_to   = spl_arr[2]; }
                if ((spl_arr.length > 0) && (tff.action_type > 0) && (tff.s_from.length() > 0)) {
                    nameFilter.add(tff);
                }
            }
        }
        if (nameFilter.size() <= 0) {
            return false;
        }
        return true;
    }    
    
    public boolean setFilterPath(String path) {
        if (useFilter == false) {
            return true;
        }
        
        File f = new File(path);
        if (f.canRead() && f.isFile()) {
            FilterPath = path.trim();
            return true;
        } else {
            return false;
        }
    }
    
    public void setFilterUse(boolean filter) {
        useFilter = filter;
    }
    
    public void setCallbackPB(JProgressBar all, JProgressBar dir) {
        callback_jpb_all = all;
        callback_jpb_dir = dir;
    }
    
    public void setLogoUse(boolean u) {
        useLOGO = u;
    }
    
    public boolean setLogoPath(String path) {
        if (useLOGO == false) {
            return true;
        }
        
        File f = new File(path);
        if (f.canRead() && f.isFile()) {
            LogoPath = path.trim();
            return true;
        } else {
            return false;
        }
    }
    
    public boolean setTags(String tags) {
        if (tags.trim().length() < 2) {
            return false;
        }
        AllTags = tags.trim().split(","); 
        return true;
    }
    
    public boolean setSlowPostingTime(int time) {
        if ((time < 3) || (time > 999)) {
            return false;
        } else {
            SQL_images_max_days = time;
            return true;
        }
    }
    
    public boolean setResize(Double resizeValue) {
        if ((resizeValue < 0.2) || (resizeValue > 5)) {
            return false;
        }
        Resize = resizeValue;
        return true;
    } 
    
    public boolean setPreviewOptions(int previewWidth, int previewHeight) {
        if ((previewWidth < 40) || (previewHeight < 40) || (previewHeight > 500) || (previewHeight > 500)) {
            return false;
        }
        PreviewWidth = previewWidth;
        PreviewHeight = previewHeight;
        return true;
    }
    
    public boolean setGalleryRoot(String path) {
        if (new File(path).isDirectory()) {
            GalleryRoot = path.trim();
            return true;
        } else {
            return true;
        }
    }
    
    public boolean setGalleryNewRoot(String path) {
        File f = new File(path);
        
        if (f.exists() == false) {
            if (f.mkdir()) {
                GalleryNewRoot = path.trim();
                return true;
            } else {
                return false;
            }
        } else {
            if (f.isDirectory()) {
                GalleryNewRoot = path.trim();
                return true;
            } else {
                return false;
            }
        }
    }
    
    
}
