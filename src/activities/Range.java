package activities;

import java.lang.IllegalArgumentException;
import java.lang.UnsupportedOperationException;

public class Range {
	
		private int lower;
		private int upper;
		
		public Range(){
			this.upper=0;
			this.lower=1;
		}
		public Range(int x){
			this.upper=x;
			this.lower=x;
		}
		public Range(String x){
			int div=x.indexOf('-');
			this.lower= Integer.parseInt(x.substring(0,div));
			this.upper=Integer.parseInt(x.substring(div+1));
		}
		public Range(int x, int y){
			if(y<x)
				throw new IllegalArgumentException();
			this.lower=x;
			this.upper=y;	
		}
		
		public int getLower(){
			if(isEmpty())
				throw new UnsupportedOperationException();
			return this.lower;
		}
		
		public int getUpper(){
			if(isEmpty())
				throw new UnsupportedOperationException();
			return this.upper;
		}
		
		public void setLower(int x)
			 throws UnsupportedOperationException,
			 IllegalArgumentException{
			 	 if(isEmpty())
			 	 	 throw new UnsupportedOperationException("set on empty interval");
			 	 if(x>upper)
			 	 	throw new IllegalArgumentException("upper bound too small");
			 	lower=x;
		}
		
		public void setUpper(int u)throws UnsupportedOperationException,
			 IllegalArgumentException{
			 	 if(isEmpty())
			 	 	 throw new UnsupportedOperationException("set on empty interval");
			 	 if(u<lower)
			 	 	throw new IllegalArgumentException("upper bound too small");
			 	upper=u;
		}
		
		public long size(){
			return upper-lower+1;
		}
		
		public boolean isEmpty(){
			if(lower>upper)
				return true;
			return false;
		}
		
		public boolean contains(int x){
			return lower <=x && x<= upper;
		}
		
		public String toString(){
			if(isEmpty())
				return "{}";
			return lower+"-"+upper;

		}

		public boolean equals(Range r){
			return lower==r.lower && upper==r.upper;
		}
		
		public String getRange() {
			return lower+"-"+upper;
		}
		
}
