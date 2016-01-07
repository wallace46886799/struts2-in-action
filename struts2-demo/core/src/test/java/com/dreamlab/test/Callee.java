/**
 * 
 */
package com.dreamlab.test;

/**
 * @author Frank
 *
 */
public class Callee {

		public void call(){
			System.out.print("This is in call.");
			String s = null;
			protectedcall(s);
		}
		
		
		protected void protectedcall(String s){
			System.out.print("This is in  protected call.");
			s.equals("");
		}
		private void privatecall(String s){
			System.out.print("This is in  private call.");
			s.equals("");
		}

}
