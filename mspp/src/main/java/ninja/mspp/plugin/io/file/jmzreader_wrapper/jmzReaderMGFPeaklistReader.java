/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.plugin.io.file.jmzreader_wrapper;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.List;
import ninja.mspp.io.peaklistreader.AbstractPeaklistReader;
import ninja.mspp.model.dataobject.XYData;
import ninja.mspp.model.entity.PeakList;
import ninja.mspp.model.entity.PeakListHeader;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.ebi.pride.tools.mgf_parser.MgfFile;

/**
 *
 * @author masaki
 */
public class jmzReaderMGFPeaklistReader extends AbstractPeaklistReader {
    
    private MgfFile mgfFile;
    private File file;

    public jmzReaderMGFPeaklistReader(String path) throws Exception {
        super(path);
        this.file = new File(path);
        this.mgfFile = new MgfFile(this.file);
    }

    @Override
    public PeakListHeader getPeaklistHeader(String processingSoftware) throws Exception {
        String md5 = DigestUtils.md5Hex( new FileInputStream( this.file ) );
        
        PeakListHeader header = new PeakListHeader();
        header.setFilePath(file.getAbsolutePath());
        header.setFileName(file.getName());
        header.setMd5(md5);
        header.setRegistrationDate(new Timestamp(System.currentTimeMillis()));
        header.setParsingSoftware("PRIDE's jmzReader");
        header.setProcessingSoftware(processingSoftware);
        
        return header;
    }

    @Override
    public Iterable<Pair<PeakList, XYData>> getPeaklists(PeakListHeader header) throws Exception {
        return new jmzReaderMGFPeaklistIterator(this.mgfFile, header);
       
    }

    @Override
    public Integer getNumberOfPeaklists() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Long> getPeaklistIds() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeakList getPeaklistById(Long index) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public XYData getXYDataById(Long index) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() throws Exception {
        // No operation is required for jmzReader.MgfFile
    }
    
}
