load wake_1.txt;
load wake_2.txt;
load wake_3.txt;
load wake_4.txt;
load wake_5.txt;
load stage1_1.txt;
load stage1_2.txt;
load stage1_3.txt;
load stage1_4.txt;
load stage1_5.txt;
load stage2_1.txt;
load stage2_2.txt;
load stage2_3.txt;
load stage2_4.txt;
load stage2_5.txt;
load stage3_1.txt;
load stage3_2.txt;
load stage3_3.txt;
load stage3_4.txt;
load stage3_5.txt;

t=4;
switch t
	case 1
		subplot(511);
		bar(extraction(wake_1));grid on;
		subplot(512);
		bar(extraction(wake_2));grid on;
		subplot(513);
		bar(extraction(wake_3));grid on;
		subplot(514);
		bar(extraction(wake_4));grid on;
		subplot(515);
		bar(extraction(wake_5));grid on;
	case 2
		subplot(511);
		bar(extraction(stage1_1));grid on;
		subplot(512);
		bar(extraction(stage1_2));grid on;
		subplot(513);
		bar(extraction(stage1_3));grid on;
		subplot(514);
		bar(extraction(stage1_4));grid on;
		subplot(515);
		bar(extraction(stage1_5));grid on;
	case 3
		subplot(511);
		bar(extraction(stage2_1));grid on;
		subplot(512);
		bar(extraction(stage2_2));grid on;
		subplot(513);
		bar(extraction(stage2_3));grid on;
		subplot(514);
		bar(extraction(stage2_4));grid on;
		subplot(515);
		bar(extraction(stage2_5));grid on;
	case 4
		subplot(511);
		bar(extraction(stage3_1));grid on;
		subplot(512);
		bar(extraction(stage3_2));grid on;
		subplot(513);
		bar(extraction(stage3_3));grid on;
		subplot(514);
		bar(extraction(stage3_4));grid on;
		subplot(515);
		bar(extraction(stage3_5));grid on;
end