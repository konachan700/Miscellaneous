/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foximager.dao.StorageFile;
import org.foximager.storage.Storage;
import org.foximager.storage.StorageScrambler;
import org.foximager.utils.Utils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author misaki
 */
public class StorageTest {
    
    private static Storage st;
    
    public StorageTest(){
    }
    
    @BeforeClass
    public static void setUpClass() throws IOException, Exception {
        st = new Storage("default");
    }
    
    @AfterClass
    public static void tearDownClass() throws IOException {
        for (File f : new File("./default").listFiles()) {
            f.delete();
        }
        st.dispose();
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void scramble() {
        try {
            final StorageScrambler s = new StorageScrambler(new File("./default"));
            
            byte b1 = s.encodeByte((byte) 71, 0x6473729364L);
            byte b2 = s.encodeByte(b1, 0x6473729364L);
            Assert.assertEquals("Same offset, one byte", (byte) 71, b2);
            
            byte b3 = s.encodeByte((byte) 62, 0x729364L);
            byte b4 = s.encodeByte(b3, 0x647329364L);
            Assert.assertNotEquals("Different offset, one byte", (byte) 62, b4);
            
            final byte[] ba1 = new byte[255];
            Arrays.fill(ba1, (byte) 43);
            final byte[] ba2 = s.encodeBytes(ba1, 0x5434583456L);
            final byte[] ba3 = s.encodeBytes(ba2, 0x5434583456L);
            Assert.assertArrayEquals("Same offset, array of 43b", ba1, ba3);
            
        } catch (IOException ex) {
            Logger.getLogger(StorageTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.assertTrue(false);
        }
    }
    
    @Test
    public void PushPull() throws IOException {
        st.openTransaction();
        final StorageFile sf = st.save(new File("./default/testSrc/test_in.jpg"));
        st.commit();
        
        final byte[] out = st.read(sf.getBinaryMD5());
        Utils.writeFile(new File("./default/testSrc/test_out.jpg"), out);
        
        final byte[] in = Utils.readFile(new File("./default/testSrc/test_in.jpg"));
        Assert.assertArrayEquals(in, out); 
    }
    
    @Test
    public void StorageBench() {
        try {
            long m1 = System.currentTimeMillis(), m2;

            st.openTransaction();
            final Random r = new Random();

            for(int i=0; i<128; i++) {
                final byte[] 
                        b = new byte[1024 * 1024];
                r.nextBytes(b);
                st.save(b, "null.png");
            }
 
            st.commit();
            
            m2 = System.currentTimeMillis();
            System.err.println("\n\n@Test StorageNonEncrypt - Executing time: " + ((m2 - m1) / 1000) + "; 1 el time: " + ((m2 - m1)/128) + "\n");
            System.err.println("Records count: "+st.getRecordsCount()+"\n\n");

            Assert.assertTrue(true);
        } catch (IOException ex) {
            Logger.getLogger(StorageTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.assertTrue(false);
        } 
    }
}
