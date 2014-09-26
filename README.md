odroid-frequency-change
=======================

An Android app that allows easy change of the Odroid CPU, GPU and Memory frequencies.


##Files Used

//CPU  
FILE_CPU_CURRENT_SCALING_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";  
FILE_CPU_SCALING_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_setspeed";  
FILE_CPU_SCALING_GOVERNER = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";  
FILE_CPU_AVAILABLE_FREQS = "/sys/devices/system/cpu/cpufreq/iks-cpufreq/freq_table";  
FILE_CPU_AVAILABLE_GOVERNERS = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";  

//GPU  
FILE_GPU_CURRENT_FREQ = "/sys/devices/platform/pvrsrvkm.0/sgx_dvfs_cur_clk";  
FILE_GPU_MIN_FREQ = "/sys/devices/platform/pvrsrvkm.0/sgx_dvfs_min_lock";  
FILE_GPU_MAX_FREQ = "/sys/devices/platform/pvrsrvkm.0/sgx_dvfs_max_lock";  
FILE_GPU_AVAILABLE_FREQS = "/sys/devices/platform/pvrsrvkm.0/sgx_dvfs_table";  

//Mem  
FILE_MEM_CURRENT_FREQ = "/sys/devices/platform/exynos5-busfreq-mif/devfreq/exynos5-busfreq-mif/cur_freq";  
FILE_MEM_MIN_FREQ = "/sys/devices/platform/exynos5-busfreq-mif/devfreq/exynos5-busfreq-mif/min_freq";  
FILE_MEM_MAX_FREQ = "/sys/devices/platform/exynos5-busfreq-mif/devfreq/exynos5-busfreq-mif/max_freq";  
FILE_MEM_AVAILABLE_FREQS = "/sys/devices/platform/exynos5-busfreq-mif/devfreq/exynos5-busfreq-mif/freq_table";  
