import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class experiment2 {
    double[][] throughputCSV=new double[10][2];
    double[][] dropRateCSV=new double[10][2];
    double[][] avgDelayCSV=new double[10][2];
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
			String tp1=null;
			String tp2=null;
			switch(t){
			case 0:
				tp1="Newreno";
				tp2="Vegas";
				break;
			case 1:
				tp1="Newreno";
				tp2="Reno";
				break;
			case 2:
				tp1="Reno";
				tp2="Reno";
				break;
			case 3:
				tp1="Vegas";
				tp2="Vegas";
				break;
			}
			
			File file = new File("experiment2/Compare_Between"+tp1+"-And-"+tp2+"-With_CBR_"+(mbps+1)+"000000-Q-35.tr");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			String[] s;
			double numBits1=0;
			double numBits2=0;
			double pktSend1=0;
			double pktRecev1=0;
			double [] a1=new double[100000];
			double pktCount1=0;
			double pktSend2=0;
			double pktRecev2=0;
			double [] a2=new double[100000];
			double pktCount2=0;
			double latency1=0;
			double latency2=0;
			while ((line = bufferedReader.readLine()) != null) {
				s=line.split(" ");
				
				if(s[0].equals("r")&&s[4].equals("tcp")&&s[2].equals("2")&&s[3].equals("3")&&s[8].equals("0.0")&&s[9].equals("3.0"))
				{
					//Deduct the header which equals to 20.
					numBits1+=8*(Integer.parseInt(s[5])-20);
				}
				if(s[0].equals("r")&&s[4].equals("tcp")&&s[2].equals("2")&&s[3].equals("5")&&s[8].equals("4.0")&&s[9].equals("5.0"))
				{
					//Deduct the header which equals to 20.
					numBits2+=8*(Integer.parseInt(s[5])-20);
				}
				
				if (s[0].equals("+") && s[4].equals( "tcp") && s[2].equals("0") && s[3].equals("1") && s[8].equals("0.0") && s[9].equals( "3.0")) 
				{
					pktSend1++;
				}

				if (s[0].equals( "r") && s[4].equals( "tcp") && s[2].equals("2") && s[3].equals("3") && s[8].equals( "0.0") && s[9].equals( "3.0")) 
				{
					pktRecev1++;
				}
				if (s[0].equals("+") && s[4].equals( "tcp") && s[2].equals("4") && s[3].equals("1") && s[8].equals("4.0") && s[9].equals( "5.0")) 
				{
					pktSend2++;
				}

				if (s[0].equals( "r") && s[4].equals( "tcp") && s[2].equals("2") && s[3].equals("5") && s[8].equals( "4.0") && s[9].equals( "5.0")) 
				{
					pktRecev2++;
				}

				if (s[0].equals( "-") && s[4].equals( "tcp") && s[2].equals("0") && s[3].equals("1") && s[8].equals("0.0") && s[9].equals( "3.0")) 
				{
					a1[Integer.parseInt(s[10])] = Double.parseDouble(s[1]);
				}

				if (s[0].equals( "r") && s[4].equals( "ack") && s[2].equals("1") && s[3].equals("0") && s[8].equals( "3.0") && s[9].equals( "0.0")) 
				{
					if (Double.parseDouble(s[1]) > a1[Integer.parseInt(s[10])]) {
						pktCount1 += 1;
						latency1 += Double.parseDouble(s[1]) - a1[Integer.parseInt(s[10])];
					}  
				}
				if (s[0].equals( "-") && s[4].equals( "tcp") && s[2].equals("4") && s[3].equals("1") && s[8].equals("4.0") && s[9].equals( "5.0")) 
				{
					a2[Integer.parseInt(s[10])] = Double.parseDouble(s[1]);
				}

				if (s[0].equals( "r") && s[4].equals( "ack") && s[2].equals("1") && s[3].equals("4") && s[8].equals( "5.0") && s[9].equals( "4.0")) 
				{
					if (Double.parseDouble(s[1]) > a2[Integer.parseInt(s[10])]) {
						pktCount2 += 1;
						latency2 += Double.parseDouble(s[1]) - a2[Integer.parseInt(s[10])];
					}  
				}
			}
			double throughput1 = numBits1/((60 - 10) * 1024);
			double throughput2 = numBits2/((60 - 5) * 1024);
			double dropRate1=0;
			double dropRate2=0;
			if (pktSend1 != 0) {
				dropRate1=(pktSend1-pktRecev1)/pktSend1;
			}
			if (pktSend2 != 0) {
				dropRate2=(pktSend2-pktRecev2)/pktSend2;
			}
			double avgDelay1=0;
			double avgDelay2=0;
			if (pktCount1 != 0) {
				 avgDelay1 = latency1/pktCount1;
			}
			if (pktCount2 != 0) {
				 avgDelay2 = latency2/pktCount2;
			}
			throughputCSV[mbps][0]=throughput1;
			throughputCSV[mbps][1]=throughput2;
			dropRateCSV[mbps][0]=dropRate1;
			dropRateCSV[mbps][1]=dropRate2;
			avgDelayCSV[mbps][0]=avgDelay1;
			avgDelayCSV[mbps][1]=avgDelay2;
			if(mbps==9)
			{
			writeCSV(throughputCSV,tp1,tp2,"throughput");
			writeCSV(dropRateCSV,tp1,tp2,"dropRate");
			writeCSV(avgDelayCSV,tp1,tp2,"avgDelay");
			}
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
	private void writeCSV(double csv[][], String name1,String name2,String tp) {
		  FileOutputStream out = null;
		try {
			out = new FileOutputStream("experiment2_"+name1+"And"+name2+tp+".csv");
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
						bw.append(","+name1+","+name2+",");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
	    		else
	    		{
				for(int j=0;j<3;j++)
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
		experiment2 ep2=new experiment2();
		for(int i=0;i<10;i++)
		{
			for(int j=0;j<4;j++)
				ep2.readFile(i,j);
		}
	}
}