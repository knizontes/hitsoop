package main_pack;

import java.io.IOException;
import java.lang.Character.Subset;
import java.util.StringTokenizer;





import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;



public class HitSoopMapReduce {

	public static class AflMapper extends Mapper<Object, Text, Text, Text>{
		
		private Text textKey= new Text();
		private Text outputVal= new Text();
		private HitSoop afl= new HitSoop(1000000);
		private Boolean  eof=false;
		private Boolean begun=false;
		
		public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
//			System.out.println("Input reading...");
			if (!begun){
				System.out.println("mapper started...");
				begun=true;
			}
			
			String inputString= new String(value.toString());
			if (inputString.length()==0)
				return;
			String outputString;
			if (inputString.contains("EOF")){
				System.out.println("EOF read!");
				textKey.set("grafo");
				
				String [] output = (afl.navigationalScketchToStringVerbose().toString()).split("\n");
				for (int i=0; i<output.length;++i){
					outputVal.set(output[i]);
					context.write(textKey,outputVal);
				}
				return;

			}

			afl.add_brother_list(inputString);
		}
	}
	
	public static class AFLReducer extends Reducer<Text, Text, Text, Text>{
		private Text out= new Text();
		private HitSoop afl = new HitSoop(1000000);
		
		public void reduce(Text key, Iterable<Text> values, 
                Context context) throws IOException, InterruptedException {	
			for (Text val : values) {
//				System.out.println(val.toString());
		        afl.add_brother_list(val.toString());
//		        context.write(key,val);
		      }

			out.set(afl.navigationalScketchToStringComplete().toString());
			key.set("#grafo");
			context.write(key,out);
//			out.set(afl.navigationalScketchToStringVerbose().toString());
			context.write(key,new Text("#Finito!"));
		}
	}
	
	public static class AFLCombiner extends Reducer<Text, Text, Text, Text>{
		private Text out= new Text();
		private HitSoop afl = new HitSoop(1000000);
		
		public void reduce(Text key, Iterable<Text> values, 
                Context context) throws IOException, InterruptedException {	
			for (Text val : values) {
//				System.out.println(val.toString());
		        afl.add_brother_list(val.toString());
//		        context.write(key,val);
		      }

			String [] output = (afl.navigationalScketchToStringVerbose().toString()).split("\n");
			key.set("#grafo");
			for (int i=0; i<output.length;++i){
				out.set(output[i]);
				context.write(key,out);
			}
//			out.set(afl.navigationalScketchToStringVerbose().toString());
			context.write(key,new Text("#Finito!"));
		}
	}
	
	public static void main(String[] args) throws Exception {
	    Configuration conf = new Configuration();
	    Job job = new Job(conf, "Hit the Soop!!!");
	    job.setJarByClass(HitSoopMapReduce.class);
	    job.setMapperClass(AflMapper.class);
	    job.setCombinerClass(AFLCombiner.class);
	    job.setReducerClass(AFLReducer.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(Text.class);
	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job, new Path(args[1]));
	    System.out.println("Beginning Hit Soop job...");
	    System.exit(job.waitForCompletion(true) ? 0 : 1);
	  }
	
	
	
	
//	public int getRawNum(String s){
//		int retval=0;
//		for (int i=0; i<s.length();++i)
//			if (s.charAt(i)=='\n')
//				++retval;
//		return retval;
//	}
//	
//	public String getRaw(String s,int index){
//		int counter=0;
//		int beginIndex,endIndex;
//		Boolean b=true;
//		beginIndex=0;
//		endIndex=0;
//		for (int i=0; i<s.length();++i){
//			if (s.charAt(i)=='\n')
//				++counter;
//			if (counter==index){
//				beginIndex=i;
//				break;
//			}
//		}
//		for (int i=beginIndex; i<s.length();++i)
//			if (s.charAt(i)=='\n'||s.charAt(i)=='\0'){
//				endIndex=i;
//				break;
//			}
//		return s.substring(beginIndex, endIndex);
//		
//	}
	
}
