import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class experiment3 {
    double[][] throughputRenoCSV=new double[80][4];
    double[][] throughputSackCSV=new double[80][4];
    double[][] avgDelayRenoCSV=new double[80][4];
    double[][] avgDelaySackCSV=new double[80][4];
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
	private void readFile(int t){
		try {
			String tp1=null;
			String tp2=null;
			switch(t){
			case 0:
				tp1="Reno";
				tp2="DropTail";
				break;
			case 1:
				tp1="Reno";
				tp2="RED";
				break;
			case 2:
				tp1="Sack1";
				tp2="DropTail";
				break;
			case 3:
				tp1="Sack1";
				tp2="RED";
				break;
			}
			
			int[] st1=new int[100000];
			int[] st2=new int[100000];
			int[] et1=new int[100000];
			int[] et2=new int[100000];
			for(int i=0;i<80;i++)
			{
			File file = new File("experiment3/Experiment3-"+tp1+"-With-Q-"+tp2+".tr");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			String[] s;
			double throughput1=0;
			double throughput2=0;
			double msn1 = 0;
			int mrsn1 = 0;
			double pc1=0;
			double td1=0;
			double msn2 = 0;
			int mrsn2 = 0;
			double pc2=0;
			double td2=0;
			int largest1=0;
			int largest2=0;

			
			while ((line = bufferedReader.readLine()) != null) {
				s=line.split(" ");
				
				if(s[0].equals("r")&&s[4].equals("tcp")&&s[2].equals("2")&&s[3].equals("3")&&s[8].equals("0.0")&&s[9].equals("3.0")&&(Double.parseDouble(s[1])<=i+1)&&(Double.parseDouble(s[1])>i))
				{
					//Deduct the header which equals to 20.
					throughput1+=8*(Integer.parseInt(s[5]));
					
				}
				if(s[0].equals("r")&&s[4].equals("cbr")&&s[2].equals("2")&&s[3].equals("5")&&s[8].equals("4.0")&&s[9].equals("5.0")&&(Double.parseDouble(s[1])<=i+1)&&(Double.parseDouble(s[1])>i))
				{
					//Deduct the header which equals to 20.
					throughput2+=8*(Integer.parseInt(s[5]));					
				}
				
				if (s[0].equals( "-") && s[4].equals( "tcp") && s[2].equals("0") && s[3].equals("1") && s[8].equals("0.0") && s[9].equals( "3.0")&&(Integer.parseInt(s[10])>=msn1)) 
				{
					largest1=Integer.parseInt(s[10]);
					st1[largest1]=i;
				}

				if (s[0].equals( "r") && s[4].equals( "tcp") && s[2].equals("2") && s[3].equals("3") && s[8].equals( "0.0") && s[9].equals( "3.0")&&(Integer.parseInt(s[10])>=msn1)) 
				{
					pc1++;
					mrsn1=Integer.parseInt(s[10]);
					et1[mrsn1]=i;
					td1+=Math.abs(Double.parseDouble(s[1])-st1[mrsn1]);					
				}
                           
    				if (s[0].equals( "-") && s[4].equals( "cbr") && s[2].equals("4") && s[3].equals("1") && s[8].equals("4.0") && s[9].equals( "5.0")&&(Integer.parseInt(s[10])>=msn2)) 
    				{
    					largest2=Integer.parseInt(s[10]);
    					st2[largest2]=i;
    				}

    				if (s[0].equals( "r") && s[4].equals( "cbr") && s[2].equals("2") && s[3].equals("5") && s[8].equals( "4.0") && s[9].equals( "5.0")&&(Integer.parseInt(s[10])>=msn2)) 
    				{
    					pc2++;
    					mrsn2=Integer.parseInt(s[10]);
    					et2[mrsn2]=i;
    					td2+=Math.abs(Double.parseDouble(s[1])-st2[mrsn2]);
    				}
                
			}
			 

			double avgDelay1=0;
			if (pc1 != 0) {
				 avgDelay1 = td1/pc1;
			}
			double avgDelay2=0;
			if (pc2 != 0) {
				 avgDelay2 = td2/pc2;
			}
			if(t<2)
			{
			throughputRenoCSV[i][2*t]=throughput1/1024/1024;
			throughputRenoCSV[i][2*t+1]=throughput2/1024/1024;
			avgDelayRenoCSV[i][2*t]=avgDelay1;
			avgDelayRenoCSV[i][2*t+1]=avgDelay2;
			}
			else
			{
				throughputSackCSV[i][2*t-4]=throughput1/1024/1024;
				throughputSackCSV[i][2*t-3]=throughput2/1024/1024;
				avgDelaySackCSV[i][2*t-4]=avgDelay1;
				avgDelaySackCSV[i][2*t-3]=avgDelay2;
			}
			fileReader.close();
		}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Write the analyzed data into the CSV file
	 * Let the EXCEL to draw the graph
	 * /
	 */
	private void writeCSV(double csv[][], String variant,String name) {
		  FileOutputStream out = null;
		try {
			out = new FileOutputStream("experiment3_"+variant+name+".csv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        OutputStreamWriter osw=new OutputStreamWriter(out);
	        BufferedWriter bw=new BufferedWriter(osw);
	    	for(int i=0;i<81;i++)
			{
	    		if(i==0)
	    		{
	    			try {
						bw.append(","+variant+"_TCP_Droptail,"+variant+"_CBR_Droptail,"+variant+"_TCP_RED,"+variant+"_CBR_RED,");
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
							bw.append(i+"s,");
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
		experiment3 ep3=new experiment3();

			for(int j=0;j<4;j++)
			{
				ep3.readFile(j);
			}
		ep3.writeCSV(ep3.throughputRenoCSV,"Reno","Throughput");
		ep3.writeCSV(ep3.avgDelayRenoCSV,"Reno","AvgDelay");
		ep3.writeCSV(ep3.throughputSackCSV,"Sack","Throughput");
		ep3.writeCSV(ep3.avgDelaySackCSV,"Sack","AvgDelay");
	}
}