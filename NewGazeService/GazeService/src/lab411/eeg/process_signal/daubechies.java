package lab411.eeg.process_signal;
public class daubechies extends liftbase {
  final static double sqrt3 = Math.sqrt( 3 );
  final static double sqrt2 = Math.sqrt( 2 );

  protected void normalize( double[] S, int N, int direction )
  {
    int half = N >> 1;

    for (int n = 0; n < half; n++) {
      if (direction == forward) {
	S[n] = ((sqrt3-1.0)/sqrt2) * S[n];
	S[n+half] = ((sqrt3+1.0)/sqrt2) * S[n+half];
      }
      else if (direction == inverse) {
	S[n] = ((sqrt3+1.0)/sqrt2) * S[n];
	S[n+half] = ((sqrt3-1.0)/sqrt2) * S[n+half];
      }
      else {
	System.out.println("daubechies::normalize: bad direction value");
	break;
      }
    }
  } // normalize


  protected void predict( double[] S, int N, int direction )
  {
    int half = N >> 1;

    if (direction == forward) {
      S[half] = S[half] - (sqrt3/4.0)*S[0] - (((sqrt3-2)/4.0)*S[half-1]);
    }
    else if (direction == inverse) {
      S[half] = S[half] + (sqrt3/4.0)*S[0] + (((sqrt3-2)/4.0)*S[half-1]);
    }
    else {
      System.out.println("daubechies::predict: bad direction value");
    }

    // predict, forward

    for (int n = 1; n < half; n++) {
      if (direction == forward) {
	S[half+n] = S[half+n] - (sqrt3/4.0)*S[n] - (((sqrt3-2)/4.0)*S[n-1]);
      }
      else if (direction == inverse) {
	S[half+n] = S[half+n] + (sqrt3/4.0)*S[n] + (((sqrt3-2)/4.0)*S[n-1]);
      }
      else {
	break;
      }
    }

  }  // predict


  protected void updateOne(  double[] S, int N, int direction )
  {
    int half = N >> 1;

    for (int n = 0; n < half; n++) {
      double updateVal = sqrt3 * S[half+n];

      if (direction == forward) {
	S[n] = S[n] + updateVal;
      }
      else if (direction == inverse) {
	S[n] = S[n] - updateVal;
      }
      else {
	System.out.println("daubechies::updateOne: bad direction value");
	break;
      }
    }
  } // updateOne



  protected void update(  double[] S, int N, int direction )
  {
    int half = N >> 1;

    for (int n = 0; n < half-1; n++) {
      if (direction == forward) {
	S[n] = S[n] - S[half+n+1];
      }
      else if (direction == inverse) {
	S[n] = S[n] + S[half+n+1];
      }
      else {
	System.out.println("daubechies::update: bad direction value");
	break;
      }
    }

    if (direction == forward) {
      S[half-1] = S[half-1] - S[half];
    }
    else if (direction == inverse) {
      S[half-1] = S[half-1] + S[half];
    }
  } // update


  public double[] forwardTrans( double[] vec )
  {
    final int N = vec.length;
// DWT level 3
    for (int n = N; n > 31; n = n >> 1) {  //for (int n = N; n > 1; n = n >> 1)
      split( vec, n );
      updateOne( vec, n, forward );  // update 1
      predict( vec, n, forward );
      update( vec, n, forward );     // update 2 
      normalize( vec, n, forward );
    }
    return vec;
  } // forwardTrans



  /**
    <p>
    Default two step Lifting Scheme inverse wavelet transform
    </p>

    <p>
    inverseTrans is passed the result of an ordered wavelet 
    transform, consisting of an average and a set of wavelet
    coefficients.  The inverse transform is calculated
    in-place and the result is returned in the argument array.
    </p>

   */
  public void inverseTrans( double[] vec )
  {
    final int N = vec.length;

    for (int n = 2; n <= N; n = n << 1) {
      normalize( vec, n, inverse );
      update( vec, n, inverse );
      predict( vec, n, inverse );
      updateOne(vec, n, inverse );
      merge( vec, n );
    }
  } // inverseTrans
}
