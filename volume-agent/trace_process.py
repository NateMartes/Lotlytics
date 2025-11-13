import psutil
import time
import sys
from datetime import datetime

def monitor_process(pid, interval=1, duration=None):
    """
    Monitor CPU and memory usage of a process.
    
    Args:
        pid: Process ID to monitor
        interval: Sampling interval in seconds (default: 1)
        duration: Total monitoring duration in seconds (default: infinite)
    """
    try:
        process = psutil.Process(pid)
        print(f"Monitoring process: {process.name()} (PID: {pid})")
        print(f"{'Timestamp':<20} {'CPU %':<10} {'Memory %':<12} {'Memory (MB)':<15}")
        print("-" * 60)
        
        start_time = time.time()
        
        while True:
            # Check if duration limit reached
            if duration and (time.time() - start_time) >= duration:
                break
            
            # Get process metrics
            cpu_percent = process.cpu_percent(interval=0.1)
            memory_info = process.memory_info()
            memory_percent = process.memory_percent()
            memory_mb = memory_info.rss / (1024 * 1024)  # Convert to MB
            
            # Print current stats
            timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            print(f"{timestamp:<20} {cpu_percent:<10.2f} {memory_percent:<12.2f} {memory_mb:<15.2f}")
            
            time.sleep(interval)
            
    except psutil.NoSuchProcess:
        print(f"Error: Process with PID {pid} not found.")
        sys.exit(1)
    except psutil.AccessDenied:
        print(f"Error: Access denied to process {pid}. Try running with sudo.")
        sys.exit(1)
    except KeyboardInterrupt:
        print("\nMonitoring stopped by user.")
        sys.exit(0)

def find_process_by_name(name):
    """Find process ID by name."""
    processes = []
    for proc in psutil.process_iter(['pid', 'name']):
        if name.lower() in proc.info['name'].lower():
            processes.append(proc)
    return processes

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage:")
        print("  By PID:  python monitor.py <pid> [interval] [duration]")
        print("  By Name: python monitor.py --name <process_name>")
        print("\nExample: python monitor.py 1234 2 60")
        print("         python monitor.py --name firefox")
        sys.exit(1)
    
    if sys.argv[1] == "--name":
        if len(sys.argv) < 3:
            print("Error: Please provide process name")
            sys.exit(1)
        
        processes = find_process_by_name(sys.argv[2])
        if not processes:
            print(f"No process found with name containing '{sys.argv[2]}'")
            sys.exit(1)
        
        if len(processes) > 1:
            print("Multiple processes found:")
            for p in processes:
                print(f"  PID: {p.pid}, Name: {p.info['name']}")
            print("\nPlease specify PID directly.")
            sys.exit(0)
        
        pid = processes[0].pid
    else:
        try:
            pid = int(sys.argv[1])
        except ValueError:
            print("Error: PID must be an integer")
            sys.exit(1)
    
    interval = float(sys.argv[2]) if len(sys.argv) > 2 else 1
    duration = float(sys.argv[3]) if len(sys.argv) > 3 else None
    
    monitor_process(pid, interval, duration)
