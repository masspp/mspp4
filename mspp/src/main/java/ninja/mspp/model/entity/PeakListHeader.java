package ninja.mspp.model.entity;

import java.io.Serializable;
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
 * The persistent class for the PEAKLIST_HEADER database table.
 *
 */
@Entity
@Table(name="PEAKLIST_HEADERS")
@NamedQuery(name="PEAKLIST_HEADER.findAll", query="SELECT s FROM PeakListHeader s")
public class PeakListHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

        @OneToMany(mappedBy="peaklistheader")
	private List<PeakList> peaklists;

	//bi-directional many-to-one association to Sample
	@ManyToOne
	private Sample sample;

	public PeakListHeader() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
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
            return peaklists;
        }

        /**
         * @param peaklists the peaklists to set
         */
        public void setPeakLists(List<PeakList> peaklists) {
            this.peaklists = peaklists;
        }
        
        public PeakList addPeakLists(PeakList peaklist){
            getPeakLists().add(peaklist);
            peaklist.setPeakListHeader(this);
            return peaklist;   
        }
        
        public PeakList removePeakLists(PeakList peaklist){
            getPeakLists().remove(peaklist);
            peaklist.setPeakListHeader(null);
            return peaklist;
        }

}