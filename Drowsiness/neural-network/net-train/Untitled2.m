load wake_1.txt;
load stage1_4.txt;
load stage2_3.txt;
load stage3_1.txt;

subplot(411);
bar(extraction(wake_1))
grid on
title('Wake')
subplot(412);
bar(extraction(stage1_4))
grid on;
title('Stage 1')
subplot(413);
bar(extraction(stage2_3))
grid on;
title('Stage 2')
subplot(414);
bar(extraction(stage3_1))
xlabel('Features (E1 E2 E3 E4 E5 E6 E7)')
grid on;
title('Stage 3')