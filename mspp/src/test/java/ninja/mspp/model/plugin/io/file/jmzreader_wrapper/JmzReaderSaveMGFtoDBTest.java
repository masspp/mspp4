/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.model.plugin.io.file.jmzreader_wrapper;

import java.util.ArrayList;
import java.util.Map.Entry;

import java.io.File;
import java.net.URL;
import java.util.List;
import ninja.mspp.model.entity.Peak;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
        
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
        
import ninja.mspp.model.entity.PeakList;
import ninja.mspp.repository.PeakListRepository;
import ninja.mspp.plugin.io.file.jmzreader_wrapper.jmzReaderMGFInputPlugin;
import uk.ac.ebi.pride.tools.mgf_parser.MgfFile;
import uk.ac.ebi.pride.tools.mgf_parser.model.Ms2Query;

/**
 *
 * @author masakimu
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class JmzReaderSaveMGFtoDBTest {
    
    private String test_file_path;
    
    private jmzReaderMGFInputPlugin plugin;
    
    @Autowired
    private TestEntityManager testEntityManager;
    
    @Autowired
    private PeakListRepository peaklistRepository;
    
    public JmzReaderSaveMGFtoDBTest(){
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
    public void savePeakList() throws Exception {
        PeakList peaklist = new PeakList();
        peaklist.setIndex(Long.valueOf(1) );
        peaklist.setMsStage(1);
        peaklist.setTitle("HOGEHOGE");
        peaklist.setScanNo(Long.valueOf(2));
        testEntityManager.persistAndFlush(peaklist);
        List<PeakList> pls = peaklistRepository.findAll();
        assertEquals("HOGEHOGE", pls.get(0).getTitle());
        //assertEquals("HOGEHOGE", peaklist.getTitle());
    }
    

    //TODO: update for new PeakListHeader nad PeakList entity classes
    @Test
    public void testGeneratePeaklistfromMGF() throws Exception {      
        MgfFile mgfFile = new MgfFile( new File(test_file_path));
        
        Long count = Long.valueOf(0);
        for (Ms2Query q: mgfFile.getMs2QueryIterator()){
            count++;
            PeakList peaklist = new PeakList();
            peaklist.setIndex(count);
             
            // Obtain Peaklist Properties
            Property props = getPropertiesbyTitle(q.getTitle());
            props = UpdatePropertiesByAPI(q, props);  
            
            peaklist.setScanNo(props.index);
            peaklist.setMsStage(props.msStage);
            peaklist.setRt(props.rt);
            peaklist.setPrecursorMz(props.precursorMz);
            peaklist.setPrecursorCharge(props.charge);
            peaklist.setTitle(props.title);
            peaklist.setPeaks(new ArrayList<Peak>());
            testEntityManager.persist(peaklist);
            
            // Set Peak Data
            ArrayList<Peak> peaks = new ArrayList<>();
            for( Entry<Double,Double> mgfpeak : q.getPeakList().entrySet() )
            {
                Peak peak = new Peak();
                peak.setPeakPosition(mgfpeak.getKey());
                peak.setIntensity(mgfpeak.getValue());
                peaklist.addPeak(peak);
            };
            testEntityManager.persist(peaklist);
        }
        
        List<PeakList> pls = peaklistRepository.findAll();
        assertEquals(4, pls.size());
        assertEquals(Long.valueOf(152), pls.get(1).getScanNo());
        List<Peak> peaks2 = pls.get(1).getPeaks();
        //peaks2.sort((p1, p2)-> Double.compare(p1.getPeakPosition(), p2.getPeakPosition()));
        assertEquals(260.19671, peaks2.get(1).getPeakPosition(), 0.0);
        assertEquals(19970.9529724121, peaks2.get(1).getIntensity(), 0.0);
        assertEquals( (Integer)3, pls.get(1).getPrecursorCharge() );

    }
    
    protected class Property{
        
        public Property(){
            index=null;
            msStage=null;
            rt=null;
            precursorMz=null;
            charge=null;
            title=null;
        }
    
        private Long index;

        private Integer msStage;

        private String rt;

        private Double precursorMz;

        private Integer charge;

        private String title;
    }
    
        /**
     * 
     * @param q
     * @param props
     * @return 
     */
    private Property UpdatePropertiesByAPI(Ms2Query q, Property props){

        if (props.index ==null){
            props.index=Long.parseLong(q.getId());  // Take Care: This is not Scan No
        }
        if (props.msStage==null){
            props.msStage=q.getMsLevel();
        }
        if (props.rt==null){
            props.rt=q.getRetentionTime();
        }
        if (props.precursorMz == null){
            props.precursorMz = q.getPrecursorMZ();
        }
        if (props.charge == null){
            props.charge = q.getPrecursorCharge();
        }
    
        return props;
    }
    
    /**
     * extract meta information from peaklist title
     * 
     * @param title
     * @return 
     */
    private Property getPropertiesbyTitle(String title){
 
        if (title ==null){
           title = "This is test. spec_id: 22515, sample_index: 0, sample_name: MS, file_path: D://hogehoge\\^10.242.132.48\\^taba@jp\\ManualInputFile\\150211tk04-whole_2m8h-4.raw, spec_rt: 114.08659, spec_prec: 494.301454039255, spec_stage: 2, charge: 1, Precursor: 0 _multi_, polarity: 1 - Scan22515, File: 150211tk04-whole_2m8h-4.raw";
        }
        
        Property props = new Property();
        props.title = title;
        
        // extract sepc_id
        String spec_id_str = "spec_id: ";  // TODO: use property file to specify matching strings
        int spec_id_start = title.indexOf(spec_id_str)+spec_id_str.length();
        int spec_id_end = spec_id_start + title.substring(spec_id_start).indexOf(",");
        if (spec_id_start > -1 && spec_id_end >-1){
            props.index = Long.parseLong(title.substring(spec_id_start, spec_id_end));
            //System.out.println("Title: " + title+ "\n");
            //System.out.println(spec_id_str+ props.index );
        }
        
        // extract spec_rt
        String spec_rt_str = "spec_rt: ";
        int spec_rt_start = title.indexOf(spec_rt_str )+spec_rt_str.length();
        int spec_rt_end = spec_rt_start + title.substring(spec_rt_start).indexOf(",");
        if (spec_rt_start > -1 && spec_rt_end>-1){
            props.rt = title.substring(spec_rt_start,spec_rt_end);
            //System.out.println(spec_rt_str + props.rt.toString() );
        }
        
        // extract precursor mz
        String spec_prec_str = "spec_prec: ";
        int spec_prec_start = title.indexOf(spec_prec_str)+spec_prec_str.length();
        int spec_prec_end = spec_prec_start + title.substring(spec_prec_start).indexOf(",");
        if (spec_prec_start > -1 && spec_prec_end > -1){
            props.precursorMz = Double.parseDouble(title.substring(spec_prec_start,spec_prec_end));
            //System.out.println(spec_prec_str + props.precursorMz.toString());
        }
        
        // extract MS stage
        String spec_stage_str = "spec_stage: ";
        int spec_stage_start = title.indexOf(spec_stage_str)+spec_stage_str.length();
        int spec_stage_end = spec_stage_start + title.substring(spec_stage_start).indexOf(",");
        if (spec_stage_start > -1 && spec_stage_end >-1){
            props.msStage = Integer.parseInt(title.substring(spec_stage_start,spec_stage_end));
            //System.out.println(spec_stage_str + props.msStage.toString() );
        }
        
        
        // extract precursor charge
        String charge_str = "charge: ";
        int charge_start = title.indexOf(charge_str)+charge_str.length();
        int charge_end = charge_start + title.substring(charge_start).indexOf(",");
        if (charge_start > -1 && charge_end >-1){
            props.charge = Integer.parseInt(title.substring(charge_start,charge_end));
            //System.out.println(charge_str + props.charge );
        }   
        
        return props;
        
    }
    
}
