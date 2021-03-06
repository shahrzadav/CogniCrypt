scope({c0_ABYApp:2, c0_Enum:2});
defaultScope(1);
intRange(-8, 7);
stringLength(35);

c0_Enum = Abstract("c0_Enum");
c0_ABYApp = Abstract("c0_ABYApp");
c0_Euclid = Clafer("c0_Euclid").withCard(1, 1);
c0_Millionaire = Clafer("c0_Millionaire").withCard(1, 1);
c0_Task = Abstract("c0_Task");
c0_description = c0_Task.addChild("c0_description").withCard(1, 1);
c0_SECMUPACOMP = Clafer("c0_SECMUPACOMP").withCard(1, 1);
c0_aby = c0_SECMUPACOMP.addChild("c0_aby").withCard(1, 1);
c0_security = c0_SECMUPACOMP.addChild("c0_security").withCard(1, 1);
c0_ABYApp.extending(c0_Enum);
c0_Euclid.extending(c0_ABYApp);
c0_Millionaire.extending(c0_ABYApp);
c0_description.refTo(string);
c0_SECMUPACOMP.extending(c0_Task);
c0_SECMUPACOMP.addConstraint(equal(joinRef(join($this(), c0_description)), constant("\"Anonymous Multi-Party Computation\"")));
c0_aby.refTo(c0_ABYApp);
c0_security.refTo(Int);
c0_SECMUPACOMP.addConstraint(implies(equal(joinRef(join($this(), c0_aby)), global(c0_Euclid)), or(or(or(or(equal(joinRef(join($this(), c0_security)), constant(80)), equal(joinRef(join($this(), c0_security)), constant(112))), equal(joinRef(join($this(), c0_security)), constant(128))), equal(joinRef(join($this(), c0_security)), constant(192))), equal(joinRef(join($this(), c0_security)), constant(256)))));
c0_SECMUPACOMP.addConstraint(implies(equal(joinRef(join($this(), c0_aby)), global(c0_Millionaire)), equal(joinRef(join($this(), c0_security)), constant(128))));
