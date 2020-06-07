package ninja.mspp.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.dsl.BooleanExpression;

import ninja.mspp.model.entity.Group;
import ninja.mspp.model.entity.GroupSample;
import ninja.mspp.model.entity.Project;
import ninja.mspp.model.entity.QGroup;
import ninja.mspp.model.entity.QGroupSample;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.repository.GroupRepository;
import ninja.mspp.repository.GroupSampleRepository;
import ninja.mspp.repository.SampleRepository;

@Service
public class IdentificationService {
	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupSampleRepository groupSampleRepository;

	@Autowired
	private SampleRepository sampleRepository;

	public List<File> getRawDataFiles(Project project) {
		List<File> files = new ArrayList<File>();

		List<Group> groups = new ArrayList<Group>();
		QGroup qGroup = QGroup.group;
		BooleanExpression expression = qGroup.project.eq(project);
		for(Group group : this.groupRepository.findAll(expression)) {
			groups.add(group);
		}

		List<GroupSample> groupSamples = new ArrayList<GroupSample>();
		QGroupSample qGroupSample = QGroupSample.groupSample;
		for(Group group : groups) {
			expression = qGroupSample.group.eq(group);
			for(GroupSample groupSample : this.groupSampleRepository.findAll(expression)) {
				groupSamples.add(groupSample);
			}
		}

		List<Sample> samples = new ArrayList<Sample>();
		for(GroupSample groupSample : groupSamples) {
			samples.add(groupSample.getSample());
		}

		for(Sample sample : samples) {
			File file = new File(sample.getFilepath());
			files.add(file);
		}
		return files;
	}

	public File callProteoWizard(
			File rawData,
			String command,
			String method,
			boolean deisotope,
			double minSpacing,
			double snRatio
	) throws Exception {
		File file = File.createTempFile(rawData.getName(), ".mgf", new File("."));

		List<String> list = new ArrayList<String>();
		list.add(command);
		list.add("-v");
		list.add("--mgf");
		list.add("--outfile");
		list.add(file.getAbsolutePath());
		list.add("--filter");
		list.add("msLevel 2-2");
		list.add("--filter");
		list.add(String.format("peakPicking %s snr=%.3f peakSpace=%.3f", method, snRatio, minSpacing));
		if(deisotope) {
			list.add("--filter");
			list.add("MS2Deisotope Poisson");
		}
		list.add("--filter");
		list.add("titleMaker <RunId>.<ScanNumber>.<ChargeState> File: <SourcePath>, NativeID:<Id>");
		list.add(rawData.getAbsolutePath());

		try {
			String[] array = new String[list.size()];
			array = list.toArray(array);
			ProcessBuilder builder = new ProcessBuilder(array);
			Process process = builder.start();
			int result = process.waitFor();
			if(result > 0) {
				file = null;
			}
			process.destroy();
			Thread.sleep(5000);
		}
		catch(Exception e) {
			e.printStackTrace();
			file = null;
		}
		return file;
	}

	public File processCometSearch(
			String command,
			File mgfFile,
			boolean decoy,
			List<File> taxons,
			String enzyme,
			String enzymeTerminal,
			List<String> fixedMods,
			List<String> variableMods,
			int maxVariableMods,
			int requireVariableMods,
			int maxParentCharge,
			int maxFragmentCharge,
			int termDistance,
			double minParentMz,
			double maxParentMz,
			double peptideTol,
			String unit,
			double fragmentBinTol,
			double fragmentBinOffset,
			String theoreticalFragmentIons,
			String isotopeError,
			int minCleavage,
			boolean aIon,
			boolean bIon,
			boolean cIon,
			boolean xIon,
			boolean yIon,
			boolean zIon
	) throws IOException, InterruptedException {
		File file = new File(mgfFile.getParent(), mgfFile.getName().replace(".mgf", ".pep.xml"));
		File parameterFile = this.createCometParameterFile(
				mgfFile, decoy, taxons, enzyme, enzymeTerminal, fixedMods, variableMods, maxVariableMods,
				requireVariableMods, maxParentCharge, maxFragmentCharge, termDistance, minParentMz, maxParentMz,
				peptideTol, unit, fragmentBinTol, fragmentBinOffset, theoreticalFragmentIons, isotopeError,
				minCleavage, aIon, bIon, cIon, xIon, yIon, zIon
		);

		List<String> args = new ArrayList<String>();
		args.add(command);
		args.add("-P" + parameterFile.getAbsolutePath());
		args.add(mgfFile.getAbsolutePath());

		for(String arg : args) {
			System.out.print(String.format("\"%s\" ", arg));
		}
		System.out.println("");

		String[] array = new String[args.size()];
		array = args.toArray(array);

		ProcessBuilder builder = new ProcessBuilder(array);
		Process process = builder.start();
		process.waitFor();
		process.destroy();

		return file;
	}

	private File createCometParameterFile(
			File mgfFile,
			boolean decoy,
			List<File> taxons,
			String enzyme,
			String enzymeTerminal,
			List<String> fixedMods,
			List<String> variableModes,
			int maxVariableMods,
			int requireVariableMods,
			int maxParentCharge,
			int maxFragmentCharge,
			int termDistance,
			double minParentMz,
			double maxParentMz,
			double peptideTol,
			String unit,
			double fragmentBinTol,
			double fragmentBinOffset,
			String TheoreticalFragmentIons,
			String isotopeError,
			int minCleavage,
			boolean aIon,
			boolean bIon,
			boolean cIon,
			boolean xIon,
			boolean yIon,
			boolean zIon
	) throws IOException {
		File file = File.createTempFile("coment_parameters", ".txt");

		PrintWriter writer = new PrintWriter(new FileWriter(file));

		writer.println("# comet_version 2019.01 rev. 5");
		writer.println("num_threads = -2");
		writer.println("max_index_runtime = 0");

		List<String> elements = new ArrayList<String>();
		for(File taxonFile : taxons) {
			elements.add(taxonFile.getAbsolutePath());
		}
		writer.println("database_name = " + String.join(" ",  elements));

		int value = 0;
		if(decoy) {
			value = 1;
		}
		writer.println("decoy_search = " + value);
		writer.println("peff_format = 0");
		writer.println("peff_obo = peff\\PSI-MOD.obo");

		writer.println("peptide_mass_tolerance = " + peptideTol);
		value = 0;
		if(unit.equals("mnu")) {
			value = 1;
		}
		else if(unit.equals("ppm")) {
			value = 2;
		}
		writer.println("peptide_mass_units=" + value);
		writer.println("mass_type_parent = 1");
		writer.println("mass_type_fragment = 1");
		writer.println("precursor_tolerance_type = 0");

		value = 1;
		if(isotopeError.equals("0, +1, +2 Isotope Offsets")) {
			value = 2;
		}
		else if(isotopeError.equals("0, +1, +2, +3 Isotope Offsets")) {
			value = 3;
		}
		else if(isotopeError.equals("Searches -8, -4, 0, +4, +8 Isotope Offsets")) {
			value = 4;
		}
		else if(isotopeError.equals("-1, 0, +1, +2, +3 Isotope Offsets")) {
			value = 5;
		}
		writer.println("isotope_error = " + value);
		writer.println("fragment_bin_tol=" + fragmentBinTol);
		writer.println("fragment_bin_offset=" + fragmentBinOffset);

		writer.println("search_enzyme_number=1");
		writer.println("search_enzyme2_number = 0");
		writer.println("allowed_missed_cleavage=1");

		writer.println("add_A_alanine=0.000000");
		writer.println("add_B_user_amino_acid=0.000000");
		writer.println("add_C_cysteine=57.021464  # \"Carbamidomethyl (C)\"");
		writer.println("add_Cterm_peptide=0.000000");
		writer.println("add_Cterm_protein=0.000000");
		writer.println("add_D_aspartic_acid=0.000000");
		writer.println("add_E_glutamic_acid=0.000000");
		writer.println("add_F_phenylalanine=0.000000");
		writer.println("add_G_glycine=0.000000");
		writer.println("add_H_histidine=0.000000");
		writer.println("add_I_isoleucine=0.000000");
		writer.println("add_J_user_amino_acid=0.000000");
		writer.println("add_K_lysine=0.000000");
		writer.println("add_L_leucine=0.000000");
		writer.println("add_M_methionine=0.000000");
		writer.println("add_N_asparagine=0.000000");
		writer.println("add_Nterm_peptide=0.000000");
		writer.println("add_Nterm_protein=0.000000");
		writer.println("add_O_ornithine=0.000000");
		writer.println("add_P_proline=0.000000");
		writer.println("add_Q_glutamine=0.000000");
		writer.println("add_R_arginine=0.000000");
		writer.println("add_S_serine=0.000000");
		writer.println("add_T_threonine=0.000000");
		writer.println("add_U_user_amino_acid=0.000000");
		writer.println("add_V_valine=0.000000");
		writer.println("add_W_tryptophan=0.000000");
		writer.println("add_X_user_amino_acid=0.000000");
		writer.println("add_Y_tyrosine=0.000000");
		writer.println("add_Z_user_amino_acid=0.000000");

		writer.println("variable_mod01=15.994915 M 0 5 -1 0 0  # 'Oxidation (M)'");
		writer.println("variable_mod02=0.000000 X 0 3 -1 0 0");
		writer.println("variable_mod03=0.000000 X 0 3 -1 0 0");
		writer.println("variable_mod04=0.000000 X 0 3 -1 0 0");
		writer.println("variable_mod05=0.000000 X 0 3 -1 0 0");
		writer.println("variable_mod06=0.000000 X 0 3 -1 0 0");
		writer.println("variable_mod07=0.000000 X 0 3 -1 0 0");
		writer.println("variable_mod08=0.000000 X 0 3 -1 0 0");
		writer.println("variable_mod09=0.000000 X 0 3 -1 0 0");

		writer.println("max_variable_mods_in_peptide=" + maxVariableMods);
		writer.println("require_variable_mod = " + requireVariableMods);


		writer.println("theoretical_fragment_ions=");
		writer.println("precursor_NL_ions");
		writer.println("mass_offsets=");

		writer.println("use_A_ions = " + (aIon ? 1 : 0));
		writer.println("use_B_ions = " + (bIon ? 1 : 0));
		writer.println("use_C_ions = " + (cIon ? 1 : 0));
		writer.println("use_X_ions = " + (xIon ? 1 : 0));
		writer.println("use_Y_ions = " + (yIon ? 1 : 0));
		writer.println("use_Z_ions = " + (zIon ? 1 : 0));
		writer.println("use_NL_ions = 1");

		writer.println("num_output_lines=1");
		writer.println("output_outfiles=0");
		writer.println("output_pepxmlfile=1");
		writer.println("output_percolatorfile=0");
		writer.println("output_sqtfile=0");
		writer.println("output_sqtstream=0");
		writer.println("output_txtfile=0");
		writer.println("print_expect_score=1");
		writer.println("show_fragment_ions=0");

		value = 0;
		if(enzyme.equals("Trypsin")) {
			value = 1;
		}
		else if(enzyme.equals("Trypsin/P")) {
			value = 2;
		}
		else if(enzyme.equals("Lys C")) {
			value = 3;
		}
		else if(enzyme.equals("Lys N")) {
			value = 4;
		}
		else if(enzyme.equals("Arg C")) {
			value = 5;
		}
		else if(enzyme.equals("Asp N")) {
			value = 6;
		}
		else if(enzyme.equals("CNBr")) {
			value = 7;
		}
		else if(enzyme.equals("Glu C")) {
			value = 8;
		}
		else if(enzyme.equals("PepsinA")) {
			value = 9;
		}
		else if(enzyme.equals("NoCleavage")) {
			value = 10;
		}
		writer.println("sample_enzyme_number=" + value);

		writer.println("activation_method = ALL");
		writer.println("scan_range=0");
		writer.println("max_fragment_charge=" + maxFragmentCharge);
		writer.println("max_precursor_charge=" + maxParentCharge);
		writer.println("precursor_charge=0 0");
		writer.println("override_charge  = 0");
		writer.println("ms_level=2");
		writer.println("minimum_peaks=10");
		writer.println("minimum_intensity = 0");

		writer.println("remove_precursor_peak=0");
		writer.println("remove_precursor_tolerance=1.5");
		writer.println("clear_mz_range=0.000000 0.000000");

		writer.println("num_results=100");
		writer.println("output_suffix=");
		writer.println("clip_nterm_methionine=0");
		writer.println("decoy_prefix=DECOY_");
		writer.println("digest_mass_range=600.000000 5000.000000");
		writer.println("equal_I_and_L = 1");
		writer.println("max_duplicate_proteins = 0");
		writer.println("nucleotide_reading_frame=0");
		writer.println("peff_verbose_output  = 0");
		writer.println("spectrum_batch_size=20000");
		writer.println("");
		writer.println("[COMET_ENZYME_INFO]");
		writer.println("\t0.  No_enzyme              0      -           -");
		writer.println("\t1.  Trypsin                1      KR          P");
		writer.println("\t2.  Trypsin/P              1      KR          -");
		writer.println("\t3.  Lys_C                  1      K           P");
		writer.println("\t4.  Lys_N                  0      K           -");
		writer.println("\t5.  Arg_C                  1      R           P");
		writer.println("\t6.  Asp_N                  0      D           -");
		writer.println("\t7.  CNBr                   1      M           -");
		writer.println("\t8.  Glu_C                  1      DE          P");
		writer.println("\t9.  PepsinA                1      FL          P");
		writer.println("\t10. NoCleavage             1      J           P");

		writer.close();

		return file;
	}
}
