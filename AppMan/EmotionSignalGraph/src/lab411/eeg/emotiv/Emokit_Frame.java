/*
 * <Copyright project =Samsung_Reran; file=samsung.lab411.emotiv> 
 * 
 * <company> HUST-Laboratory 411-Samsung EEG project</company>
 * 
 * <author> PhongTran </author>
 * 
 * <email> phongtran0715@gmail.com@gmail.com </email>
 * 
 * <date> Apr 29, 2014 - 3:18:25 PM </date>
 * 
 * <purpose> </purpose>
 */

package lab411.eeg.emotiv;
public class Emokit_Frame {
	public float F3, FC6, P7, T8, F7, F8, T7, P8, AF4, F4, AF3, O2, O1, FC5;

	/**
	 * Channel of Headset
	 */
	public Emokit_Frame() {
		F3 = 0;
		FC6 = 0;
		P7 = 0;
		T8 = 0;
		F7 = 0;
		F8 = 0;
		T7 = 0;
		P8 = 0;
		AF4 = 0;
		F4 = 0;
		AF3 = 0;
		O2 = 0;
		O1 = 0;
		FC5 = 0;
	}

	/**
	 * Show infomations
	 * @param k
	 */
	public static void show(Emokit_Frame k) {
		System.out.println("F3: " + k.F3);
		System.out.println("FC6: " + k.FC6);
		System.out.println("P7: " + k.P7);
		System.out.println("T8: " + k.T8);
		System.out.println("F7: " + k.F7);
		System.out.println("F8: " + k.F8);
		System.out.println("T7: " + k.T7);
		System.out.println("P8: " + k.P8);
		System.out.println("AF4: " + k.AF4);
		System.out.println("F4: " + k.F4);
		System.out.println("AF3: " + k.AF3);
		System.out.println("O2: " + k.O2);
		System.out.println("O1: " + k.O1);
		System.out.println("FC5: " + k.FC5);
	}
}
