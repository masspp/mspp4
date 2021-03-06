/*
 * BSD 3-Clause License
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * @author Mass++ Users Group (https://www.mspp.ninja/)
 * @author Masayo Kimura
 * @since 2019
 *
 * Copyright (c) 2019 Masayo Kimura
 * All rights reserved.
 */
package ninja.mspp.model.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class to save metadata of peak list.
 *
 */
@Entity
@Table(name="PEAKLIST_HEADERS")
@NamedQuery(name="PEAKLIST_HEADER.findAll", query="SELECT s FROM PeakListHeader s")
public class PeakListHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
    
        @OneToMany(mappedBy="peaklistheader")
	private List<PeakList> peaklists;

        /**
         * TODO: review how to link peaklist header to project and sample lator.
         */
        @ManyToOne
	private Project project;

        /**
         *  just reserved
         */
	//bi-directional many-to-one association to Sample
	@ManyToOne
	private Sample sample;
        
        /**
         * software name used for peak detection
         */
        @Column
        private String processingSoftware;
        
        /**
         * software name used to import peaklist.
         */
        @Column
        private String parsingSoftware;
        
        /**
         * filePath of peaklist file (expect full filePath)
         */
        @Column
        private String filePath;
        
        @Column
        private String fileName;
        
        /**
         * checksum of peak list file.
         */
        @Column
        private String md5;
        
	@Column(name="REGISTRATION_DATE")
	private Timestamp registrationDate;
        
        /**
         * qeury type like MIS, PMF, or something. //TODO: define enum or something?
         */
        @Column
        private String queryType;
        
        /**
         * peak list file format: "MGF", etc.  //TODO: define enum or something.
         */
        @Column
        private String fileformat;
        

	public PeakListHeader() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public Sample getSample() {
		return this.sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}
        
        /**
        * @return the peaklists
        */
        public List<PeakList> getPeakLists() {
            return getPeaklists();
        }

        /**
         * @param peaklists the peaklists to set
         */
        public void setPeakLists(List<PeakList> peaklists) {
            this.setPeaklists(peaklists);
        }

    /**
     * @return the md5
     */
    public String getMd5() {
        return md5;
    }

    /**
     * @param md5 the md5 to set
     */
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * @return the registrationDate
     */
    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    /**
     * @param registrationDate the registrationDate to set
     */
    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }

    /**
     * @return the queryType
     */
    public String getQueryType() {
        return queryType;
    }

    /**
     * @param queryType the queryType to set
     */
    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    /**
     * @return the fileformat
     */
    public String getFileformat() {
        return fileformat;
    }

    /**
     * @param fileformat the fileformat to set
     */
    public void setFileformat(String fileformat) {
        this.fileformat = fileformat;
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the peaklists
     */
    public List<PeakList> getPeaklists() {
        return peaklists;
    }

    /**
     * @param peaklists the peaklists to set
     */
    public void setPeaklists(List<PeakList> peaklists) {
        this.peaklists = peaklists;
    }
    
    
    public PeakList addPeaklist(PeakList peaklist){
        this.getPeaklists().add(peaklist);
        peaklist.setPeakListHeader(this);
        return peaklist;
    }
    
    public PeakList removePeaklist(PeakList peaklist){
        getPeaklists().remove(peaklist);
        peaklist.setPeakListHeader(null);
        return peaklist;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    /**
     * @return the processingSoftware
     */
    public String getProcessingSoftware() {
        return processingSoftware;
    }

    /**
     * @param processingSoftware the processingSoftware to set
     */
    public void setProcessingSoftware(String processingSoftware) {
        this.processingSoftware = processingSoftware;
    }


    /**
     * @return the parsingSoftware
     */
    public String getParsingSoftware() {
        return parsingSoftware;
    }

    /**
     * @param parsingSoftware the parsingSoftware to set
     */
    public void setParsingSoftware(String parsingSoftware) {
        this.parsingSoftware = parsingSoftware;
    }

}