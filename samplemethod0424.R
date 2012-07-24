w_a<-0.8;
w_t<-0.2;
pat<-c(0.7,0.7,0.7,0.9);
index<-c();
age<-c();
salary<-c();
score<-c();
geo<-c();
accept<-c();
pvs.age<-c();
pvs.salary<-c();
pvs.score<-c();
pvs.geo<-c();

######The above is to initialize the parameter.########

agebinno<-5;
agesbin<-c(0.2,0.4,0.6,0.8,1);#0.2 coressonding to 18-25, 0.4 to 26 - 35, 0.6 to 35 - 45, 0.8 to 46 - 60, 1 to 60+;
size<-300;
agesimulate<-runif(size,min=0,max=1);

salarybinno<-5;
salarybin<-c(0.2,0.4,0.6,0.8,1);
salarysimulate<-rnorm(size,mean=0.5,sd=0.16);

scorebinno<-5;
scorebin<-c(0.2,0.4,0.6,0.8,1);
scoresimulate<-rnorm(size,mean=0.5,sd=0.16);


geobinno<-40;
geobin<-seq(0,1,by=1/geobinno)[1:geobinno+1];
geosimulate<-rnorm(size,mean=0.5,sd=0.08);

#####The above code is to simulate the data#########

for(i in 1:size)
{
	index<-c(index,i);
	age<-c(age,which.max(agesimulate[i]<agesbin));
	salary<-c(salary,which.max(salarysimulate[i]<salarybin));
	score<-c(score,which.max(scoresimulate[i]<scorebin));
	geo<-c(geo,which.max(geosimulate[i]<geobin));
	accept<-c(accept,0);#assume all apps are rejected.
}

#####Fit the data to the right bin#########
app<-data.frame(age,salary,score,geo,accept);

tphist.age<-c(2,2,2,2,2);#initialize the prior.
aphist.age<-c(1,1,1,1,1);
rphist.age<-c(1,1,1,1,1);

tphist.salary<-c(2,2,2,2,2);
aphist.salary<-c(1,1,1,1,1);
rphist.salary<-c(1,1,1,1,1);

tphist.score<-c(2,2,2,2,2);
aphist.score<-c(1,1,1,1,1);
rphist.score<-c(1,1,1,1,1);

tphist.geo<-rep(2,geobinno);
aphist.geo<-rep(1,geobinno);
rphist.geo<-rep(1,geobinno);



for(i in 1:nrow(app))
{
	tphist.age[app[i,'age']]<-tphist.age[app[i,'age']]+1;#update the distribution for true population
	tphist.salary[app[i,'salary']]<-tphist.salary[app[i,'salary']]+1;
	tphist.score[app[i,'score']]<-tphist.score[app[i,'score']]+1;
	tphist.geo[app[i,'geo']]<-tphist.geo[app[i,'geo']]+1;

	pv<-c(1-aphist.age[app[i,'age']]/sum(aphist.age),1-aphist.salary[app[i,'salary']]/sum(aphist.salary),1-aphist.score[app[i,'score']]/sum(aphist.score),1-aphist.geo[app[i,'geo']]/sum(aphist.geo))*w_a+c(1-tphist.age[app[i,'age']]/sum(tphist.age),1-tphist.salary[app[i,'salary']]/sum(tphist.salary),1-tphist.score[app[i,'score']]/sum(tphist.score),1-tphist.geo[app[i,'geo']]/sum(tphist.geo))*w_t;
	pvs.age<-c(pvs.age,pv[1]);
	pvs.salary<-c(pvs.salary,pv[2]);
	pvs.score<-c(pvs.score,pv[3]);
	pvs.geo<-c(pvs.geo,pv[4]);
	
	votes<-sum(pv>pat);
	if(votes>3) {app[i,'accept'] = 1;aphist.age[app[i,'age']]<-aphist.age[app[i,'age']]+1;aphist.salary[app[i,'salary']]<-aphist.salary[app[i,'salary']]+1;aphist.score[app[i,'score']]<-aphist.score[app[i,'score']]+1;aphist.geo[app[i,'geo']]<-aphist.geo[app[i,'geo']]+1;}
	else{app[i,'accept']=0;rphist.age[app[i,'age']]<-rphist.age[app[i,'age']]+1;rphist.salary[app[i,'salary']]<-rphist.salary[app[i,'salary']]+1;rphist.score[app[i,'score']]<-rphist.score[app[i,'score']]+1;rphist.geo[app[i,'geo']]<-rphist.geo[app[i,'geo']]+1;}
}

pvdf<-data.frame(index,pvs.age,pvs.salary,pvs.score,pvs.geo);

aphist.age
tphist.age

barplot(tphist.age)
barplot(aphist.age)
barplot(aphist.salary)
barplot(tphist.salary)
barplot(aphist.salary)
barplot(tphist.score)
barplot(aphist.score)
barplot(aphist.geo)
barplot(tphist.score)

targetAcceptDist<-data.frame(tphist.age,aphist.age,tphist.salary,aphist.salary,tphist.score,aphist.score);

Passw0rd01
Passw0rd01
