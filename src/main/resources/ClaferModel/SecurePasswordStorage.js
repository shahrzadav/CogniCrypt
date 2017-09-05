scope({c0_Algorithm:2, c0_Enum:9, c0_Performance:4, c0_Security:5, c0_name:2, c0_performance:2, c0_security:2, c1_description:2});
defaultScope(1);
intRange(-8, 7);
stringLength(36);

c0_Enum = Abstract("c0_Enum");
c0_Security = Abstract("c0_Security");
c0_NoSecurity = Clafer("c0_NoSecurity").withCard(1, 1);
c0_Broken = Clafer("c0_Broken").withCard(1, 1);
c0_Weak = Clafer("c0_Weak").withCard(1, 1);
c0_Medium = Clafer("c0_Medium").withCard(1, 1);
c0_Strong = Clafer("c0_Strong").withCard(1, 1);
c0_Performance = Abstract("c0_Performance");
c0_VerySlow = Clafer("c0_VerySlow").withCard(1, 1);
c0_Slow = Clafer("c0_Slow").withCard(1, 1);
c0_Fast = Clafer("c0_Fast").withCard(1, 1);
c0_VeryFast = Clafer("c0_VeryFast").withCard(1, 1);
c0_Task = Abstract("c0_Task");
c0_description = c0_Task.addChild("c0_description").withCard(1, 1);
c0_Algorithm = Abstract("c0_Algorithm");
c0_name = c0_Algorithm.addChild("c0_name").withCard(1, 1);
c1_description = c0_Algorithm.addChild("c1_description").withCard(1, 1);
c0_security = c0_Algorithm.addChild("c0_security").withCard(1, 1);
c0_performance = c0_Algorithm.addChild("c0_performance").withCard(1, 1);
c0_Digest = Abstract("c0_Digest");
c0_outputSize = c0_Digest.addChild("c0_outputSize").withCard(1, 1);
c0_KeyDerivationAlgorithm = Abstract("c0_KeyDerivationAlgorithm");
c0_iterations = c0_KeyDerivationAlgorithm.addChild("c0_iterations").withCard(1, 1);
c1_outputSize = c0_KeyDerivationAlgorithm.addChild("c1_outputSize").withCard(1, 1);
c0_digest = c0_KeyDerivationAlgorithm.addChild("c0_digest").withCard(0, 1);
c0_SHA = Clafer("c0_SHA").withCard(1, 1);
c0_pbkdf2 = Clafer("c0_pbkdf2").withCard(1, 1);
c0_SecurePassword = Clafer("c0_SecurePassword").withCard(1, 1);
c0_kda = c0_SecurePassword.addChild("c0_kda").withCard(1, 1);
c1_security = c0_SecurePassword.addChild("c1_security").withCard(1, 1);
c0_Security.extending(c0_Enum).refToUnique(Int);
c0_NoSecurity.extending(c0_Security);
Constraint(implies(some(global(c0_NoSecurity)), equal(joinRef(global(c0_NoSecurity)), constant(0))));
c0_Broken.extending(c0_Security);
Constraint(implies(some(global(c0_Broken)), equal(joinRef(global(c0_Broken)), constant(1))));
c0_Weak.extending(c0_Security);
Constraint(implies(some(global(c0_Weak)), equal(joinRef(global(c0_Weak)), constant(2))));
c0_Medium.extending(c0_Security);
Constraint(implies(some(global(c0_Medium)), equal(joinRef(global(c0_Medium)), constant(3))));
c0_Strong.extending(c0_Security);
Constraint(implies(some(global(c0_Strong)), equal(joinRef(global(c0_Strong)), constant(4))));
c0_Performance.extending(c0_Enum).refToUnique(Int);
c0_VerySlow.extending(c0_Performance);
Constraint(implies(some(global(c0_VerySlow)), equal(joinRef(global(c0_VerySlow)), constant(1))));
c0_Slow.extending(c0_Performance);
Constraint(implies(some(global(c0_Slow)), equal(joinRef(global(c0_Slow)), constant(2))));
c0_Fast.extending(c0_Performance);
Constraint(implies(some(global(c0_Fast)), equal(joinRef(global(c0_Fast)), constant(3))));
c0_VeryFast.extending(c0_Performance);
Constraint(implies(some(global(c0_VeryFast)), equal(joinRef(global(c0_VeryFast)), constant(4))));
c0_description.refTo(string);
c0_name.refTo(string);
c1_description.refTo(string);
c0_security.refTo(c0_Security);
c0_performance.refTo(c0_Performance);
c0_Digest.extending(c0_Algorithm);
c0_outputSize.refTo(Int);
c0_KeyDerivationAlgorithm.extending(c0_Algorithm);
c0_iterations.refTo(Int);
c1_outputSize.refTo(Int);
c0_digest.refTo(c0_Digest);
c0_KeyDerivationAlgorithm.addConstraint(implies(some(join($this(), c0_digest)), equal(joinRef(join($this(), c0_security)), joinRef(join(joinRef(join($this(), c0_digest)), c0_security)))));
c0_KeyDerivationAlgorithm.addConstraint(equal(joinRef(join($this(), c1_outputSize)), joinRef(join(joinRef(join($this(), c0_digest)), c0_outputSize))));
c0_KeyDerivationAlgorithm.addConstraint(notEqual(joinRef(join(joinRef(join($this(), c0_digest)), c0_security)), global(c0_Broken)));
c0_KeyDerivationAlgorithm.addConstraint(equal(joinRef(join($this(), c0_iterations)), constant(1000)));
c0_SHA.extending(c0_Digest);
c0_SHA.addConstraint(equal(joinRef(join($this(), c0_name)), constant("\"SHA\"")));
c0_SHA.addConstraint(equal(joinRef(join($this(), c1_description)), constant("\"SHAdigest\"")));
c0_SHA.addConstraint(or(or(or(or(equal(joinRef(join($this(), c0_outputSize)), constant(160)), equal(joinRef(join($this(), c0_outputSize)), constant(224))), equal(joinRef(join($this(), c0_outputSize)), constant(256))), equal(joinRef(join($this(), c0_outputSize)), constant(384))), equal(joinRef(join($this(), c0_outputSize)), constant(512))));
c0_SHA.addConstraint(implies(equal(joinRef(join($this(), c0_outputSize)), constant(160)), and(equal(joinRef(join($this(), c0_performance)), global(c0_VeryFast)), equal(joinRef(join($this(), c0_security)), global(c0_Weak)))));
c0_SHA.addConstraint(implies(equal(joinRef(join($this(), c0_outputSize)), constant(224)), and(equal(joinRef(join($this(), c0_performance)), global(c0_Fast)), equal(joinRef(join($this(), c0_security)), global(c0_Strong)))));
c0_SHA.addConstraint(implies(equal(joinRef(join($this(), c0_outputSize)), constant(256)), and(equal(joinRef(join($this(), c0_performance)), global(c0_Fast)), equal(joinRef(join($this(), c0_security)), global(c0_Strong)))));
c0_SHA.addConstraint(implies(equal(joinRef(join($this(), c0_outputSize)), constant(384)), and(equal(joinRef(join($this(), c0_performance)), global(c0_Fast)), equal(joinRef(join($this(), c0_security)), global(c0_Strong)))));
c0_SHA.addConstraint(implies(equal(joinRef(join($this(), c0_outputSize)), constant(512)), and(equal(joinRef(join($this(), c0_performance)), global(c0_Slow)), equal(joinRef(join($this(), c0_security)), global(c0_Strong)))));
c0_pbkdf2.extending(c0_KeyDerivationAlgorithm);
c0_pbkdf2.addConstraint(equal(joinRef(join($this(), c0_name)), constant("\"PBKDF2\"")));
c0_pbkdf2.addConstraint(equal(joinRef(join($this(), c1_description)), constant("\"PBKDF2 key derivation\"")));
c0_pbkdf2.addConstraint(equal(joinRef(join($this(), c0_performance)), global(c0_Slow)));
c0_pbkdf2.addConstraint(some(join($this(), c0_digest)));
c0_pbkdf2.addConstraint(equal(joinRef(join($this(), c0_security)), joinRef(join(joinRef(join($this(), c0_digest)), c0_security))));
c0_SecurePassword.extending(c0_Task);
c0_SecurePassword.addConstraint(equal(joinRef(join($this(), c0_description)), constant("\"Encode password for secure storage\"")));
c0_kda.refTo(c0_KeyDerivationAlgorithm);
c1_security.refTo(Int);
c0_SecurePassword.addConstraint(equal(joinRef(join($this(), c1_security)), joinRef(joinRef(join(joinRef(join($this(), c0_kda)), c0_security)))));
