load wake_1.txt;
load stage1_4.txt;
load stage2_3.txt;
load stage3_1.txt;

subplot(411);
plot(wake_1,'-b')
title('Wake')
ylabel('uV')
subplot(412);
plot(stage1_4,'-b')
title('Stage 1')
ylabel('uV')
subplot(413);
plot(stage2_3,'-b')
title('Stage 2')
ylabel('uV')
subplot(414);
plot(stage3_1,'-b')
title('Stage 3')
ylabel('uV')
