package ninja.mspp.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
		File file = File.createTempFile(rawData.getName(), ".mgf");

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
/*
		if(deisotope) {
			list.add("--filter");
			list.add("MS2Deisotope Poisson");
		}
*/
		list.add("--filter");
		list.add("titleMaker <RunId>.<ScanNumber>.<ChargeState> File: <SourcePath>, NativeID:<Id>");
		list.add(rawData.getAbsolutePath());

		for(String arg : list) {
			System.out.print(String.format("\"%s\" ",  arg));
		}
		System.out.println("");

		System.out.println("hogehogehogheoghoehgoehogheogheoge");
		try {
			String[] array = new String[list.size()];
			array = list.toArray(array);
			ProcessBuilder builder = new ProcessBuilder(array);
			Process process = builder.start();
			boolean result = process.waitFor(300, TimeUnit.SECONDS);
			if(!result) {
				file = null;
				System.out.println("System Time out");
			}
			else {
				System.out.println("Done!!!!!!!!!!!!!!!!");
			}
			process.destroy();
		}
		catch(Exception e) {
			e.printStackTrace();
			file = null;
		}
		return file;
	}

	public File detectPeaks(
			Project project,
			String command,
			String method,
			boolean deisotope,
			double minSpacing,
			double snRatio
	) throws Exception {
		File file = File.createTempFile("peaks",  ".mgf");
		PrintWriter writer = new PrintWriter(new FileWriter(file));

		List<File> files = this.getRawDataFiles(project);
		for(File rawData : files) {
			File outFile = this.callProteoWizard(rawData, command, method, deisotope, minSpacing, snRatio);

			if(outFile != null) {
				String line;
				BufferedReader reader = new BufferedReader(new FileReader(outFile));
				while((line = reader.readLine()) != null) {
					writer.println(line);
					System.out.println(line);
				}
				reader.close();
				writer.println("");
			}
		}

		writer.close();

		return file;
	}
}
