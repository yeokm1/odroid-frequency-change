package com.yeokhengmeng.freqchangeodroid;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {


	public static final String COMMAND_ECHO_FORMAT = "echo %1$s > %2$s";
	public static final String COMMAND_PERMISSION_FORMAT = "chmod 777 %1$s";
	public static final String COMMAND_CAT_FORMAT = "cat %1$s";

	//CPU
	public static final String FILE_CPU_CURRENT_SCALING_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";

	public static final String FILE_CPU_SCALING_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_setspeed";
	public static final String FILE_CPU_SCALING_GOVERNER = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";

	public static final String FILE_CPU_AVAILABLE_FREQS = "/sys/devices/system/cpu/cpufreq/iks-cpufreq";
	public static final String FILE_CPU_AVAILABLE_GOVERNERS = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";

	//GPU
	public static final String FILE_GPU_CURRENT_FREQ = "/sys/devices/platform/pvrsrvkm.0/sgx_dvfs_cur_clk";

	public static final String FILE_GPU_MIN_FREQ = "/sys/devices/platform/pvrsrvkm.0/sgx_dvfs_min_lock";
	public static final String FILE_GPU_MAX_FREQ = "/sys/devices/platform/pvrsrvkm.0/sgx_dvfs_max_lock";
	public static final String FILE_GPU_AVAILABLE_FREQS = "/sys/devices/platform/pvrsrvkm.0/sgx_dvfs_table";

	//Mem
	public static final String FILE_MEM_CURRENT_FREQ = "/sys/devices/platform/exynos5-busfreq-mif/devfreq/exynos5-busfreq-mif/cur_freq";

	public static final String FILE_MEM_MIN_FREQ = "/sys/devices/platform/exynos5-busfreq-mif/devfreq/exynos5-busfreq-mif/min_freq";
	public static final String FILE_MEM_MAX_FREQ = "/sys/devices/platform/exynos5-busfreq-mif/devfreq/exynos5-busfreq-mif/max_freq";
	public static final String FILE_MEM_AVAILABLE_FREQS = "/sys/devices/platform/exynos5-busfreq-mif/devfreq/exynos5-busfreq-mif/freq_table";



	public static final String TAG = "MainActivity";


	Spinner cpuGovernorsSpinner;
	Spinner cpuSpeedSpinner;
	Spinner gpuSpeedMinSpinner;
	Spinner gpuSpeedMaxSpinner;
	Spinner memSpeedMinSpinner;
	Spinner memSpeedMaxSpinner;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		cpuGovernorsSpinner = (Spinner) findViewById(R.id.cpu_governor_selection);	
		cpuSpeedSpinner = (Spinner) findViewById(R.id.cpu_freq_selection);
		gpuSpeedMinSpinner = (Spinner) findViewById(R.id.gpu_min_selection);
		gpuSpeedMaxSpinner = (Spinner) findViewById(R.id.gpu_max_selection);
		memSpeedMinSpinner = (Spinner) findViewById(R.id.mem_min_selection);
		memSpeedMaxSpinner = (Spinner) findViewById(R.id.mem_max_selection);

	}




	public void getRoot(View v){

		List<String> dummy = new ArrayList<String>();
		dummy.add("ls");
		runAsRoot(dummy);
	}

	public void setCpuPerm(View v){
		runAndGetStaticCommandOutput(String.format(COMMAND_PERMISSION_FORMAT, FILE_CPU_SCALING_GOVERNER));
		runAndGetStaticCommandOutput(String.format(COMMAND_PERMISSION_FORMAT, FILE_CPU_SCALING_FREQ));
	}

	public void refresh(View v){
		TextView cpuGovernor = (TextView) findViewById(R.id.cpu_governor);
		cpuGovernor.setText(getCurrentCPUGovernor());

		TextView cpuFreq = (TextView) findViewById(R.id.cpu_freq);
		cpuFreq.setText(getCurrentCPUFreq());	

		TextView gpuMinFreq = (TextView) findViewById(R.id.gpu_min_freq);
		gpuMinFreq.setText(getCurrentGPUMinFreq());

		TextView gpuMaxFreq = (TextView) findViewById(R.id.gpu_max_freq);
		gpuMaxFreq.setText(getCurrentGPUMaxFreq());


		TextView memMinFreq = (TextView) findViewById(R.id.mem_min_freq);
		memMinFreq.setText(getCurrentMemMinFreq());

		TextView memMaxFreq = (TextView) findViewById(R.id.mem_max_freq);
		memMaxFreq.setText(getCurrentMemMaxFreq());

		setSpinnerData();

	}


	public void setSpinnerData(){

		String[] governors = getCPUGovernors();
		setDataToThisSpinner(cpuGovernorsSpinner, governors);


		Spinner cpuSpeedSpinner = (Spinner) findViewById(R.id.cpu_freq_selection);
		String[] cpuFreqs = getCPUFreqs();
		setDataToThisSpinner(cpuSpeedSpinner, cpuFreqs);


		Spinner gpuSpeedMinSpinner = (Spinner) findViewById(R.id.gpu_min_selection);
		String[] gpuFreqs = getGPUFreqs();
		setDataToThisSpinner(gpuSpeedMinSpinner, gpuFreqs);

		Spinner gpuSpeedMaxSpinner = (Spinner) findViewById(R.id.gpu_max_selection);
		setDataToThisSpinner(gpuSpeedMaxSpinner, gpuFreqs);



		Spinner memSpeedMinSpinner = (Spinner) findViewById(R.id.mem_min_selection);
		String[] memFreqs = getMemFreqs();
		setDataToThisSpinner(memSpeedMinSpinner, memFreqs);

		Spinner memSpeedMaxSpinner = (Spinner) findViewById(R.id.mem_max_selection);
		setDataToThisSpinner(memSpeedMaxSpinner, memFreqs);


	};


	public void setDataToThisSpinner(Spinner spinner, String[] data){
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,data);
		spinner.setAdapter(adapter);
	}

	public void applyCurrent(View v){

		List<String> rootCommands = new ArrayList<String>();
		String cpuGoverner = (String) cpuGovernorsSpinner.getSelectedItem();
		rootCommands.add(String.format(COMMAND_ECHO_FORMAT, cpuGoverner, FILE_CPU_SCALING_GOVERNER));


		String cpuFreq = (String) cpuSpeedSpinner.getSelectedItem();
		rootCommands.add(String.format(COMMAND_ECHO_FORMAT, cpuFreq, FILE_CPU_SCALING_FREQ));


		String gpuMinFreq = (String) gpuSpeedMinSpinner.getSelectedItem();
		rootCommands.add(String.format(COMMAND_ECHO_FORMAT, gpuMinFreq, FILE_GPU_MIN_FREQ));

		String gpuMaxFreq = (String) gpuSpeedMaxSpinner.getSelectedItem();
		rootCommands.add(String.format(COMMAND_ECHO_FORMAT, gpuMaxFreq, FILE_GPU_MAX_FREQ));


		String memMinFreq = (String) memSpeedMinSpinner.getSelectedItem();
		rootCommands.add(String.format(COMMAND_ECHO_FORMAT, memMinFreq, FILE_MEM_MIN_FREQ));

		String memMaxFreq = (String) memSpeedMaxSpinner.getSelectedItem();
		rootCommands.add(String.format(COMMAND_ECHO_FORMAT, memMaxFreq, FILE_MEM_MAX_FREQ));

		runAsRoot(rootCommands);
		

	}






	public String getCurrentCPUGovernor(){
		return getCurrentField(FILE_CPU_SCALING_GOVERNER, false);
	}

	public String getCurrentCPUFreq(){
		return getCurrentField(FILE_CPU_CURRENT_SCALING_FREQ, false);
	}



	public String[] getCPUGovernors(){
		return getAvailableOptionsFromFile(FILE_CPU_AVAILABLE_GOVERNERS, false);
	}

	public String[] getCPUFreqs(){
		
		List<String> freqs = new ArrayList<String>();
		
		int current = 250000;
		while(current <= 1600000){
			freqs.add(Integer.toString(current));
			if( 250000 <= current  && current <= 550000){
				current += 50000;
			} else if( 600000 <= current && current <= 1600000){
				current += 100000;
				
				if(current == 700000){
					// 700000 frequency
					current += 100000;
				}
				
			} else {
				break;
			}
		}
		String[] result = new String[freqs.size()];
		freqs.toArray(result);
		
		return result;
	}



	public String getCurrentGPUMinFreq(){
		return getCurrentField(FILE_GPU_CURRENT_FREQ, false);
	}

	public String getCurrentGPUMaxFreq(){
		return getCurrentField(FILE_GPU_CURRENT_FREQ, false);
	}

	public String[] getGPUFreqs(){
		return getAvailableOptionsFromFile(FILE_GPU_AVAILABLE_FREQS, false);
	}


	public String getCurrentMemMinFreq(){
		return getCurrentField(FILE_MEM_CURRENT_FREQ, false);
	}

	public String getCurrentMemMaxFreq(){
		return getCurrentField(FILE_MEM_CURRENT_FREQ, false);
	}

	//Root issues
	public String[] getMemFreqs(){
		return new String[]{
				"800000", "400000", "200000", "100000"
		};
	}


	public String[] getAvailableOptionsFromFile(String filename, boolean needsRoot){
		String result = getCurrentField(filename, needsRoot);
		String[] splitted = result.trim().split(" ");
		return splitted;
	}

	public String getCurrentField(String filename, boolean needsRoot){
		if(needsRoot){
			return runStaticCommandAsRoot(String.format(COMMAND_CAT_FORMAT, filename));
		} else {
			return runAndGetStaticCommandOutput(String.format(COMMAND_CAT_FORMAT, filename));
		}


	}

	public String runStaticCommandAsRoot(String command){

		
		try {
            String line;
            StringBuilder log=new StringBuilder();
            Process process = Runtime.getRuntime().exec("su");
            OutputStream stdin = process.getOutputStream();
//            InputStream stderr = process.getErrorStream();
            InputStream stdout = process.getInputStream();

            stdin.write((command + "\n").getBytes());
            stdin.write("exit\n".getBytes());
            stdin.flush();

            stdin.close();
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(stdout));
            while ((line = br.readLine()) != null) {
                Log.d("[Output]", line);
				log.append(line);
				log.append("\n");
            }
            br.close();
//            br = new BufferedReader(new InputStreamReader(stderr));
//            while ((line = br.readLine()) != null) {
//            	Log.e("[Error]", line);
//				log.append(line);
//				log.append("\n");
//            }
//            br.close();

			process.waitFor();
			process.destroy();
			

			return log.toString();
        } catch (Exception ex) {
        	return "";
        }

	}

	public void runAsRoot(List<String> cmds){
		try{
			Process p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());            
			for (String tmpCmd : cmds) {
				Log.d("TAG", "Run Root Command: " + tmpCmd);
				os.writeBytes(tmpCmd+"\n");
			}           
			os.writeBytes("exit\n");  
			os.flush();
		} catch (Exception e){

		}
	}


	private String runAndGetStaticCommandOutput(String command){
		Log.d("TAG", "Run Command: " + command);
		try {
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			StringBuilder log=new StringBuilder();
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				log.append(line);
				log.append("\n");


			}


			String result = log.toString();
			Toast.makeText(this, command + ", output: " + log, Toast.LENGTH_SHORT).show();;
			Log.d("TAG", "Run Command output: " + result);
			return result;


		}catch (IOException e) {

		}

		return "";
	}

}
