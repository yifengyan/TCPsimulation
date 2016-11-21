import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class experiment1 {
    double[][] throughputCSV=new double[10][4];
    double[][] dropRateCSV=new double[10][4];
    double[][] avgDelayCSV=new double[10][4];
	/* Read the TR file, and write data into the array.
	 * There 12 values, each of them refer below:
	    1. EVENT
  		2. TIME
  		3. SRC;
  		4. DEST;
  		5. TYPE;
  		6. SIZE;
  		7. -------;
  		8. FID;
  		9. SADDR;
  		10. DADDR;
  		11. SEQ;
  		12. PID;
	 */
	private void readFile(int mbps, int t){
		try {
			String tp=null;
			switch(t){
			case 0:
				tp="TCP";
				break;
			case 1:
				tp="Reno";
				break;
			case 2:
				tp="Newreno";
				break;
			case 3:
				tp="Vegas";
				break;
			}
			
			File file = new File("experiment1/TCP_first_"+tp+"-"+(mbps+1)+"000000.tr");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			String[] s;
			double numBits=0;
			double pktSend=0;
			double pktRecev=0;
			double [] a=new double[100000];
			double pktCount=0;
			double latency=0;
			while ((line = bufferedReader.readLine()) != null) {
				s=line.split(" ");
				
				if(s[0].equals("r")&&s[4].equals("tcp")&&s[2].equals("2")&&s[3].equals("3")&&s[8].equals("0.0")&&s[9].equals("3.0"))
				{
					//Deduct the header which equals to 20.
					numBits+=8*(Integer.parseInt(s[5])-20);
				}
				if (s[0].equals("+") && s[4].equals( "tcp") && s[2].equals("0") && s[3].equals("1") && s[8].equals("0.0") && s[9].equals( "3.0")) 
				{
					pktSend++;
				}

				if (s[0].equals( "r") && s[4].equals( "tcp") && s[2].equals("2") && s[3].equals("3") && s[8].equals( "0.0") && s[9].equals( "3.0")) 
				{
					pktRecev++;
				}

				if (s[0].equals( "-") && s[4].equals( "tcp") && s[2].equals("0") && s[3].equals("1") && s[8].equals("0.0") && s[9].equals( "3.0")) 
				{
					a[Integer.parseInt(s[10])] = Double.parseDouble(s[1]);
				}

				if (s[0].equals( "r") && s[4].equals( "ack") && s[2].equals("1") && s[3].equals("0") && s[8].equals( "3.0") && s[9].equals( "0.0")) 
				{
					if (Double.parseDouble(s[1]) > a[Integer.parseInt(s[10])]) {
						pktCount += 1;
						latency += Double.parseDouble(s[1]) - a[Integer.parseInt(s[10])];
					}  
				}
			}
			double throughput = numBits/((50 - 10) * 1024);
			double dropRate=0;
			if (pktSend != 0) {
				dropRate=(pktSend-pktRecev)/pktSend;
			}
			double avgDelay=0;
			if (pktCount != 0) {
				 avgDelay = latency/pktCount;
			}
			throughputCSV[mbps][t]=throughput;
			dropRateCSV[mbps][t]=dropRate;
			avgDelayCSV[mbps][t]=avgDelay;
			fileReader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Write the analyzed data into the CSV file
	 * Let the EXCEL to draw the graph
	 * /
	 */
	private void writeCSV(double csv[][], String name) {
		  FileOutputStream out = null;
		try {
			out = new FileOutputStream("experiment1_"+name+".csv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        OutputStreamWriter osw=new OutputStreamWriter(out);
	        BufferedWriter bw=new BufferedWriter(osw);
	    	for(int i=0;i<11;i++)
			{
	    		if(i==0)
	    		{
	    			try {
						bw.append(",Tahoe,Reno,Newreno,Vegas,");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
	    		else
	    		{
				for(int j=0;j<5;j++)
				{
					if(j==0)
					{
						try {
							bw.append(i+"M,");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
					{
					try {
						bw.append(csv[i-1][j-1]+",");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
				}
				}
				try {
					bw.append("\r");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    	try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	try {
				osw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	}
	
	
	public static void main(String[] args) {
		experiment1 ep1=new experiment1();
		for(int i=0;i<10;i++)
		{
			for(int j=0;j<4;j++)
				ep1.readFile(i,j);
		}
		ep1.writeCSV(ep1.throughputCSV,"throughput");
		ep1.writeCSV(ep1.dropRateCSV,"dropRate");
		ep1.writeCSV(ep1.avgDelayCSV,"avgDelay");
	}
}