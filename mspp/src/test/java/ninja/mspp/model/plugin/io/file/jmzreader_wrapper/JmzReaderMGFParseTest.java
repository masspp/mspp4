/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.model.plugin.io.file.jmzreader_wrapper;

import java.util.ArrayList;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.boot.test.context.SpringBootTest;

import ninja.mspp.model.dataobject.PeakListObject;
import ninja.mspp.plugin.io.file.jmzreader_wrapper.jmzReaderMGFInputPlugin;

/**
 *
 * @author masakimu
 */
@SpringBootTest
public class JmzReaderMGFParseTest {
    
    private String test_file_path;
    
    private jmzReaderMGFInputPlugin plugin;
    
    public JmzReaderMGFParseTest(){
    }
    
    @Before
    public void setUp() throws Exception {
        loadTestFile();
        plugin = new jmzReaderMGFInputPlugin();
    }
    
    private void loadTestFile() throws Exception {
        URL testFile=getClass().getClassLoader().getResource("msdata/test-mspp.mgf");
        Assert.assertNotNull("Error loading mgf test file", testFile);
        test_file_path = testFile.getPath();
        //System.out.println("test_file_path: " + test_file_path);
    }
    
    @Test
    public void ParseTestData() throws Exception {
        //System.out.println("test_file_path: " + test_file_path);
        //plugin.test_print(test_file_path);

        ArrayList<PeakListObject> peaklists = plugin.openMGF(test_file_path);
        PeakListObject pl1 = peaklists.get(0);
        assertEquals(152,pl1.getIndex());
        assertEquals(260.19671, pl1.getPeaks().getX(1), 0.0);
        assertEquals(19970.9529724121, pl1.getPeaks().getY(1), 0.0);
        

        
        
    }
    
}
