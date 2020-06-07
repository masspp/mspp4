/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.model.plugin.io.file.jmzreader_wrapper;

import java.util.ArrayList;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
   
import ninja.mspp.model.dataobject.PeakListObject;
import ninja.mspp.plugin.io.file.jmzreader_wrapper.jmzReaderMGFInputPlugin;

/**
 *
 * @author masakimu
 */
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
    }
    
    @Test
    public void ParseTestData() throws Exception {
        ArrayList<PeakListObject> peaklists = plugin.openMGF(test_file_path);
        PeakListObject pl1 = peaklists.get(4);
        assertEquals(152,pl1.getIndex());
        PeakListObject pl2 = peaklists.get(1);
        assertEquals(500.5, pl2.getPeaks().getX(1), 0.00001);
        assertEquals(900.5, pl2.getPeaks().getY(1), 0.00001);
        

    }
    
}
