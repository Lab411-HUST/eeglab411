function e=extraction(a)
e=1:7;
s=a';
N=length(s);
mean = sum(s)/N;
t=2;
switch t
    case 1
        [ca1 cd1]=dwt(s,'db2');
        [ca2 cd2]=dwt(ca1,'db2');
        [ca3 cd3]=dwt(ca2,'db2');
        [ca4 cd4]=dwt(ca3,'db2');
        [ca5 cd5]=dwt(ca4,'db2');
        [ca6 cd6]=dwt(ca5,'db2');
    case 2
        [l1 h1 l2 h2]=wfilters('db2');
        ca1=downsample(conv(s,l1),2,1);
        cd1=downsample(conv(s,h1),2,1);
        ca2=downsample(conv(ca1,l1),2,1);
        cd2=downsample(conv(ca1,h1),2,1);
        ca3=downsample(conv(ca2,l1),2,1);
        cd3=downsample(conv(ca2,h1),2,1);
        ca4=downsample(conv(ca3,l1),2,1);
        cd4=downsample(conv(ca3,h1),2,1);
        ca5=downsample(conv(ca4,l1),2,1);
        cd5=downsample(conv(ca4,h1),2,1);
        ca6=downsample(conv(ca5,l1),2,1);
        cd6=downsample(conv(ca5,h1),2,1);;
end
e(1)=sum(power(cd1,2))/N;
e(2)=sum(power(cd2,2))/N;
e(3)=sum(power(cd3,2))/N;
e(4)=sum(power(cd4,2))/N;
e(5)=sum(power(cd5,2))/N;
e(6)=sum(power(cd6,2))/N;
e(7) = sqrt(sum((power((s-mean),2)))/N);
