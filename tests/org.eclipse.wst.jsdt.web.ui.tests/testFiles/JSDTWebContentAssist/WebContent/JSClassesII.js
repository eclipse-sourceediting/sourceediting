var Computer = {
	proc: {
		
	},
	memory : {}
};

var Installed = {};

Computer.proc.IntelDualCore1=function(param1,param2) {
	this.foo1 = 42;
};
Computer.proc.IntelDualCore2=function(param3,param4) {
	this.foo2 = 42;
};
Computer.proc.IntelQuadCore = function(param5) {
	this.foo3 = 42;
};
Computer.proc.CeleronXSeries = function() {
	this.foo = 42;
};

Installed.CorelXSoftware = function() {
	this.foo = 42;
};


ID

CX

IDC

Comp

Ins

z //needed because the bar. causes compile issues