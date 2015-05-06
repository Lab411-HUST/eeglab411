package lab411.eeg.process_signal;

public class LowPassFilter {
	int N;
	double Wc;
	double[] hn;
	public double M;

	public LowPassFilter(int N, double Wc) {
		this.Wc = Wc;
		this.N = N;
		this.M = (N - 1) / 2.0;
		hn = new double[N];
	}

	public double gn(int n) {
		return (Math.sin(Wc * (n - M)) / (Math.PI * (n - M)));
	}

	private double Window_Hamming(int n) {
		return (0.54 - 0.46 * Math.cos(Math.PI * n / M));
	}

	private double Window_Cut(int n) {
		return 1.0;
	}

	public void Hamming() {
		for (int i = 0; i < N; i++) {
			if (i == M) {
				hn[i] = Wc / Math.PI;
			} else {
				hn[i] = Window_Hamming(i) * gn(i);
			}
		}
	}

	public void Cut() {
		for (int i = 0; i < N; i++) {
			if (i == M) {
				hn[i] = Wc / Math.PI;
			} else {
				hn[i] = Window_Cut(i) * gn(i);
			}
		}
	}

	public double[] Filt(double[] signal) {
		int size = N + signal.length - 1;
		int max = Math.max(N, signal.length);
		int min = Math.min(N, signal.length);
		double[] signal_convolution = new double[size];
		for (int i = 0; i < size; i++) {
			if (i < min) {
				for (int j = 0; j <= i; j++) {
					signal_convolution[i] += hn[j] * signal[i - j];
				}
			}
			if (i >= min && i < max) {
				for (int j = 0; j < min; j++) {
					if (min == N) {
						signal_convolution[i] += hn[j] * signal[i - j];
					} else if (min == signal.length) {
						signal_convolution[i] += hn[i - j] * signal[j];
					}

				}
			}
			if (i >= max) {
				for (int j = i + 1 - max; j < min; j++) {
					if (min == N) {
						signal_convolution[i] += hn[j] * signal[i - j];
					} else if (min == signal.length) {
						signal_convolution[i] += hn[i - j] * signal[j];
					}
				}
			}
		}
		return signal_convolution;
	}
}
