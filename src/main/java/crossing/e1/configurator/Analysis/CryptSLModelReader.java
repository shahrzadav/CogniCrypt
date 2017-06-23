package crossing.e1.configurator.Analysis;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.xtext.common.types.JvmExecutable;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.common.types.access.jdt.JdtTypeProviderFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Injector;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.Constants;
import crossing.e1.configurator.utilities.Utils;
import crypto.rules.CryptSLArithmeticConstraint;
import crypto.rules.CryptSLArithmeticConstraint.ArithOp;
import crypto.rules.CryptSLComparisonConstraint;
import crypto.rules.CryptSLComparisonConstraint.CompOp;
import crypto.rules.CryptSLCondPredicate;
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLConstraint.LogOps;
import crypto.rules.CryptSLForbiddenMethod;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLObject;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLSplitter;
import crypto.rules.CryptSLValueConstraint;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import de.darmstadt.tu.crossing.CryptSL.ui.internal.CryptSLActivator;
import de.darmstadt.tu.crossing.cryptSL.Aggregate;
import de.darmstadt.tu.crossing.cryptSL.ArithmeticExpression;
import de.darmstadt.tu.crossing.cryptSL.ArithmeticOperator;
import de.darmstadt.tu.crossing.cryptSL.ComparisonExpression;
import de.darmstadt.tu.crossing.cryptSL.Constraint;
import de.darmstadt.tu.crossing.cryptSL.Domainmodel;
import de.darmstadt.tu.crossing.cryptSL.EnsuresBlock;
import de.darmstadt.tu.crossing.cryptSL.Event;
import de.darmstadt.tu.crossing.cryptSL.Expression;
import de.darmstadt.tu.crossing.cryptSL.ForbMethod;
import de.darmstadt.tu.crossing.cryptSL.ForbiddenBlock;
import de.darmstadt.tu.crossing.cryptSL.Literal;
import de.darmstadt.tu.crossing.cryptSL.LiteralExpression;
import de.darmstadt.tu.crossing.cryptSL.Method;
import de.darmstadt.tu.crossing.cryptSL.Object;
import de.darmstadt.tu.crossing.cryptSL.ObjectDecl;
import de.darmstadt.tu.crossing.cryptSL.Order;
import de.darmstadt.tu.crossing.cryptSL.Par;
import de.darmstadt.tu.crossing.cryptSL.ParList;
import de.darmstadt.tu.crossing.cryptSL.PreDefinedPredicates;
import de.darmstadt.tu.crossing.cryptSL.SimpleOrder;
import de.darmstadt.tu.crossing.cryptSL.SuPar;
import de.darmstadt.tu.crossing.cryptSL.SuperType;
import de.darmstadt.tu.crossing.cryptSL.UnaryPreExpression;
import de.darmstadt.tu.crossing.cryptSL.UseBlock;
import typestate.interfaces.ICryptSLPredicateParameter;
import typestate.interfaces.ISLConstraint;

public class CryptSLModelReader {

	private int nodeNameCounter = 0;
	private List<CryptSLPredicate> predicates = null;
	private List<CryptSLForbiddenMethod> forbiddenMethods = null;
	private String clazzName = "";
	private StateMachineGraph smg = null;

	public CryptSLModelReader() throws ClassNotFoundException, CoreException, IOException {
		Injector injector = CryptSLActivator.getInstance().getInjector(CryptSLActivator.DE_DARMSTADT_TU_CROSSING_CRYPTSL);

		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);

		final IProject iproject = Utils.getIProjectFromSelection();
		if (iproject == null) {
			// if no project selected abort with error message
			Activator.getDefault().logError(null, Constants.NoFileandNoProjectOpened);
		}
		if (iproject.isOpen() && iproject.hasNature(Constants.JavaNatureID)) {
			resourceSet.setClasspathURIContext(JavaCore.create(iproject));
		}
		new JdtTypeProviderFactory(injector.getInstance(IJavaProjectProvider.class)).createTypeProvider(resourceSet);

		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);

		List<String> classNames = new ArrayList<String>();
		classNames.add("KeyGenerator");
		classNames.add("KeyPairGenerator");
		classNames.add("KeyStore");
		classNames.add("Mac");
		classNames.add("PBEKeySpec");
		classNames.add("SecretKey");
		classNames.add("SecretKeyFactory");
//		classNames.add("MessageDigest");
		classNames.add("Cipher");

		for (String className : classNames) {
			
			clazzName = className;
			Resource resource = resourceSet.getResource(URI.createPlatformResourceURI("/CryptSL Examples/src/de/darmstadt/tu/crossing/" + className + ".cryptsl", true), true);
			EcoreUtil.resolveAll(resourceSet);
			EObject eObject = resource.getContents().get(0);
			Domainmodel dm = (Domainmodel) eObject;
			EnsuresBlock ensure = dm.getEnsure();
			Map<CryptSLPredicate, SuperType> pre_preds = Maps.newHashMap();
			if (ensure != null) {
				pre_preds = getPredicates(ensure.getPred());
				predicates = Lists.newArrayList((ensure != null) ? pre_preds.keySet() : Lists.newArrayList());
			}
			smg = buildStateMachineGraph(dm.getOrder(), className);
			ForbiddenBlock forbEvent = dm.getForbEvent();
			forbiddenMethods = (forbEvent != null) ? getForbiddenMethods(forbEvent.getForb_methods()) : Lists.newArrayList();
			
			List<ISLConstraint> constraints = (dm.getReqConstraints() != null) ? buildUpConstraints(dm.getReqConstraints().getReq()) : Lists.newArrayList();
			List<Entry<String, String>> objects = getObjects(dm.getUsage());
			
			List<CryptSLPredicate> actPreds = Lists.newArrayList();
			
			for (CryptSLPredicate pred : pre_preds.keySet()) {
				SuperType cond = pre_preds.get(pred);
				if (cond == null) {
					actPreds.add(pred);
				} else {
					actPreds.add(new CryptSLCondPredicate(pred.getPredName(), pred.getParameters(), pred.isNegated(), getStatesForMethods(resolveAggregateToMethodeNames(cond))));
				}
			}
			CryptSLRule rule = new CryptSLRule(className, objects, forbiddenMethods, smg, constraints, predicates);
			storeRuletoFile(rule, className);
			//String outputURI = storeModelToFile(resourceSet, eObject, className);
			//loadModelFromFile(outputURI);
		}

	}

	private List<Entry<String, String>> getObjects(UseBlock usage) {
		List<Entry<String, String>> objects = new ArrayList<>();
		
		for (ObjectDecl obj : usage.getObjects()) {
			objects.add(new SimpleEntry<String, String>(obj.getObjectType().getIdentifier(), obj.getObjectName().getName()));
		}
		
		return objects;
	}

	private void storeRuletoFile(CryptSLRule rule, String className) {
		String filePath = "C:\\Users\\stefank3\\git\\CROSSINGAnalysis\\CryptoAnalysis\\src\\test\\resources\\" + className + ".cryptslbin";
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(filePath);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(rule);
			out.close();
			fileOut.close();
			FileInputStream fileIn = new FileInputStream(filePath);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			CryptSLRule inRule = (CryptSLRule) in.readObject();
			in.close();
			fileIn.close();

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private Map<CryptSLPredicate, SuperType> getPredicates(List<Constraint> predList) {
		Map<CryptSLPredicate, SuperType> preds = new HashMap<CryptSLPredicate, SuperType>();
		for (Constraint pred : predList) {
			List<ICryptSLPredicateParameter> variables = new ArrayList<ICryptSLPredicateParameter>();
			
			if (pred.getParList() != null) {
				for (SuPar var : pred.getParList().getParameters()) {
					if (var.getVal() != null) {
						String name = ((LiteralExpression) var.getVal().getLit().getName()).getValue().getName();
						if (name == null) {
							name = "this";
						}
						variables.add(new CryptSLObject(name));
					} else {
						variables.add(new CryptSLObject("_"));
					}
				}
			}
			String meth = pred.getPredName();
			SuperType cond = pred.getLabelCond();
			if (cond == null) {
				preds.put(new CryptSLPredicate(meth, variables, false), null);
			} else {
				preds.put(new CryptSLPredicate(meth, variables, false), cond);
			}
			
		}
		return preds;
	}

	private Set<StateNode> getStatesForMethods(List<CryptSLMethod> condMethods) {
		Set<StateNode> predGens = new HashSet<StateNode>();
		if (condMethods.size() != 0) {
			for (TransitionEdge methTrans : smg.getAllTransitions()) {
				final List<CryptSLMethod> transLabel = methTrans.getLabel();
				if (transLabel.size() > 0 && transLabel.equals(condMethods)) {
					predGens.add(methTrans.getRight());
				}
			}
		}
		return predGens;
	}

	private List<ISLConstraint> buildUpConstraints(List<Constraint> constraints) {
		List<ISLConstraint> slCons = new ArrayList<ISLConstraint>();
		for (Constraint cons : constraints) {
			ISLConstraint constraint = getConstraint(cons);
			if (constraint != null) {
				slCons.add(constraint);
			}
		}
		return slCons;
	}
	
	private String filterQuotes(String dirty) {
		return CharMatcher.anyOf("\"").removeFrom(dirty);
	}

	private ISLConstraint getConstraint(Constraint cons) {
		ISLConstraint slci = null;
		
		if (cons instanceof ArithmeticExpression) {
			ArithmeticExpression ae = (ArithmeticExpression) cons;
			ae.getOperator().toString();
			slci = new CryptSLArithmeticConstraint("0", "1", ArithOp.n);
		} else if (cons instanceof LiteralExpression) {
			LiteralExpression lit = (LiteralExpression) cons;
			List<String> parList = new ArrayList<String>();
			if (lit.getLitsleft() != null) {
				for (Literal a : lit.getLitsleft().getParameters()) {
					parList.add(filterQuotes(a.getVal()));
				}
			}
			String pred = lit.getCons().getPredName();
			if (pred != null) {
				switch (pred) {
					case "callTo" :
						List<ICryptSLPredicateParameter> methodsToBeCalled = new ArrayList<ICryptSLPredicateParameter>();
						methodsToBeCalled.addAll(resolveAggregateToMethodeNames((SuperType)((PreDefinedPredicates)lit.getCons()).getObj().get(0)));
						slci = new CryptSLPredicate(pred, methodsToBeCalled, false);
						break;
					case "noCallTo" :
						List<ICryptSLPredicateParameter> methodsNotToBeCalled = new ArrayList<ICryptSLPredicateParameter>();
						List<CryptSLMethod> resolvedMethodNames = resolveAggregateToMethodeNames((Aggregate)((PreDefinedPredicates)lit.getCons()).getObj().get(0));
						for (CryptSLMethod csm :resolvedMethodNames ) {
							forbiddenMethods.add(new CryptSLForbiddenMethod(csm, true));
							methodsNotToBeCalled.add(csm);
						}
						slci = new CryptSLPredicate(pred, methodsNotToBeCalled, false);
						break;
					case "neverTypeOf" :
					
						break;
					default:
						new RuntimeException();
				}
			} else {
				String part = lit.getCons().getPart();
				if (part != null) {
					LiteralExpression name = (LiteralExpression) lit.getCons().getLit().getName();
					CryptSLObject variable = new CryptSLObject(name.getValue().getName(), new CryptSLSplitter(Integer.parseInt(lit.getCons().getInd()), filterQuotes(lit.getCons().getSplit())));
					slci = new CryptSLValueConstraint(variable, parList);
				} else {
					LiteralExpression name = (LiteralExpression) lit.getCons().getName();
					if (name == null) {
						name = (LiteralExpression) lit.getCons().getLit().getName();
					}
					CryptSLObject variable = new CryptSLObject(name.getValue().getName());
					slci = new CryptSLValueConstraint(variable, parList);
				}
			}
		} else if (cons instanceof ComparisonExpression) {
			ComparisonExpression comp = (ComparisonExpression) cons;
			CompOp op = null;
			switch (comp.getOperator().toString()) {
				case ">":
					op = CompOp.g;
					break;
				case "<":
					op = CompOp.l;
					break;
				case ">=":
					op = CompOp.ge;
					break;
				case "<=":
					op = CompOp.le;
					break;
				default:
					op = CompOp.eq;
			}
			CryptSLArithmeticConstraint left;
			CryptSLArithmeticConstraint right;
			
			
			Constraint leftExpression = comp.getLeftExpression();
			if (leftExpression instanceof LiteralExpression) {
				left = convertLiteralToArithmetic(leftExpression);
			} else {
				left = (CryptSLArithmeticConstraint) leftExpression;
			}
			
			Constraint rightExpression = comp.getRightExpression();
			if (rightExpression instanceof LiteralExpression) {
				right = convertLiteralToArithmetic(rightExpression);
			}  else {
				ArithmeticExpression ar = (ArithmeticExpression) rightExpression;
				String leftValue = getValueOfLiteral(ar.getLeftExpression());
				String rightValue = getValueOfLiteral(ar.getRightExpression());

				ArithmeticOperator aop = ((ArithmeticOperator) ar.getOperator());
				ArithOp operator = null;
				if (aop.getPLUS() != null && !aop.getPLUS().isEmpty()) {
					operator = ArithOp.p;
				} else {
					operator = ArithOp.n;
				}
				
				right = new CryptSLArithmeticConstraint(leftValue, rightValue, operator);
			}
			slci = new CryptSLComparisonConstraint(left, right,	op);
		} else if (cons instanceof UnaryPreExpression) {
			UnaryPreExpression un = (UnaryPreExpression) cons;
			List<ICryptSLPredicateParameter> vars = new ArrayList<ICryptSLPredicateParameter>();
			Constraint innerPredicate = un.getEnclosedExpression();
			if (innerPredicate.getParList() != null) {
				for (SuPar sup : innerPredicate.getParList().getParameters()) {
					if (sup.getVal() == null) {
						vars.add(new CryptSLObject("_"));
					} else {
						LiteralExpression lit = sup.getVal();
						
						String variable = filterQuotes(((LiteralExpression) lit.getLit().getName()).getValue().getName());
						String part = sup.getVal().getPart();
						if (part != null) {
							vars.add(new CryptSLObject(variable, new CryptSLSplitter(Integer.parseInt(lit.getInd()), filterQuotes(lit.getSplit()))));
						} else {
							vars.add(new CryptSLObject(variable));
						}
					}
				}
			}
			slci = new CryptSLPredicate(innerPredicate.getPredName(), vars, true);
		} else if (cons instanceof Constraint) {
			if (cons.getPredName() != null && !cons.getPredName().isEmpty()) {
				List<ICryptSLPredicateParameter> vars = new ArrayList<ICryptSLPredicateParameter>();
				for (SuPar sup : cons.getParList().getParameters()) {
					if (sup.getVal() == null) {
						vars.add(new CryptSLObject("_"));
					} else {
						LiteralExpression lit = sup.getVal();
						
						String variable = filterQuotes(((LiteralExpression) lit.getLit().getName()).getValue().getName());
						String part = sup.getVal().getPart();
						if (part != null) {
							vars.add(new CryptSLObject(variable, new CryptSLSplitter(Integer.parseInt(lit.getInd()), filterQuotes(lit.getSplit()))));
						} else {
							vars.add(new CryptSLObject(variable));
						}
					}
				}
				slci = new CryptSLPredicate(cons.getPredName(), vars, false);
			} else {
				LogOps op = null;
				switch (cons.getOperator().toString()) {
					case "&&":
						op = LogOps.and;
						break;
					case "||":
						op = LogOps.or;
						break;
					case "=>":
						op = LogOps.implies;
						break;
					case "<=>":
						op = LogOps.eq;
						break;
					default:
						op = LogOps.and;
				}
				slci = new CryptSLConstraint(
					getConstraint(cons.getLeftExpression()), 
					getConstraint(cons.getRightExpression()), 
					op);
			}
		}

		return slci;
	}

	private CryptSLArithmeticConstraint convertLiteralToArithmetic(Constraint expression) {
		EObject name = ((LiteralExpression) expression).getCons().getName();
		return new CryptSLArithmeticConstraint(getValueOfLiteral(name), "0", crypto.rules.CryptSLArithmeticConstraint.ArithOp.p);
	}

	private String getValueOfLiteral(EObject name) {
		String value = "";
		if (name instanceof LiteralExpression) {
			SuperType preValue = ((LiteralExpression) name).getValue();
			if (preValue != null) {
				value = preValue.getName();
			} else {
				value = getValueOfLiteral(((LiteralExpression) name).getCons().getName());
			}
		} else {
			value = ((Literal) name).getVal();
		}
		return filterQuotes(value);
	}

	private List<CryptSLForbiddenMethod> getForbiddenMethods(EList<ForbMethod> methods) {
		List<CryptSLForbiddenMethod> methodSignatures = new ArrayList<CryptSLForbiddenMethod>();
		for (ForbMethod fm : methods) {
			JvmExecutable meth = fm.getJavaMeth();
			List<Entry<String, String>> pars = new ArrayList<Entry<String, String>>();
			for (JvmFormalParameter par : meth.getParameters()) {
				pars.add(new SimpleEntry<String,String>(par.getParameterType().getSimpleName(), par.getSimpleName()));
			}
			methodSignatures.add(new CryptSLForbiddenMethod(new CryptSLMethod(meth.getDeclaringType().getIdentifier() + "." + meth.getSimpleName(), pars, null), false));
		}
		return methodSignatures;
	}

	private StateMachineGraph buildStateMachineGraph(Expression order, String className) {

		StateMachineGraphBuilder smgb = new StateMachineGraphBuilder(order, className);
		StateMachineGraph smg = new StateMachineGraph(); //.buildSMG();
		smg.addNode(new StateNode("pre_init", true));
		nodeNameCounter = 0;
		iterateThroughSubtrees(smg, order, null, null);
		iterateThroughSubtreesOptional(smg, order, null, null);

		return smg;
	}

	private void iterateThroughSubtreesOptional(StateMachineGraph smg, Expression order, StateNode prevNode, StateNode nextNode) {
		Expression left = order.getLeft();
		Expression right = order.getRight();
		if (left == null && right == null) {
			return;
		}
		
		String leftElOp = left.getElementop();
		String rightElOp = right.getElementop();

		if ((left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			iterateThroughSubtreesOptional(smg, left, null, nextNode);
			iterateThroughSubtreesOptional(smg, right, null, nextNode);
		} else if ((left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			iterateThroughSubtreesOptional(smg, left, null, nextNode);
			if (rightElOp != null && rightElOp.equals("?")) {
				addSkipEdge(smg, right);
			}
		} else if (!(left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			if (leftElOp != null && leftElOp.equals("?")) {
				addSkipEdge(smg, left);
			}

			iterateThroughSubtreesOptional(smg, right, null, nextNode);
		} else if (!(left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			if (leftElOp != null && leftElOp.equals("?")) {
				addSkipEdge(smg, left);
			}

			if (rightElOp != null && rightElOp.equals("?")) {
				addSkipEdge(smg, right);
			}
		}

	}

	private void addSkipEdge(StateMachineGraph smg, Expression leaf) {
		List<TransitionEdge> tedges = new ArrayList<TransitionEdge>(smg.getEdges());
		for (TransitionEdge trans : tedges) {
			if (trans.getLabel().equals(resolveAggregateToMethodeNames(leaf.getOrderEv().get(0)))) {
				for (TransitionEdge innerTrans : tedges) {
					if (innerTrans.from().equals(trans.to())) {
						smg.addEdge(new TransitionEdge(innerTrans.getLabel(), trans.from(), innerTrans.to()));
					}
				}
			}
		}
	}

	private void loadModelFromFile(String outputURI) {
		ResourceSet resSet = new ResourceSetImpl();
		Resource xmiResourceRead = resSet.getResource(URI.createURI(outputURI), true);
		xmiResourceRead.getContents().get(0);
//		Domainmodel dmro = 
	}

	private String storeModelToFile(XtextResourceSet resourceSet, EObject eObject, String className) throws IOException {
		//Store the model to path outputURI
		String outputURI = "file:///C:/Users/stefank3/Desktop/" + className + ".xmi";
		Resource xmiResource = resourceSet.createResource(URI.createURI(outputURI));
		xmiResource.getContents().add(eObject);
		xmiResource.save(null);
		return outputURI;
	}

	private void iterateThroughSubtrees(StateMachineGraph smg, Expression order, StateNode prevNode, StateNode nextNode) {
		//if order.getLeft == null && order.getRight == null => no nesting whatsoever todo
		Expression left = order.getLeft();
		Expression right = order.getRight();
		String elementOp = order.getElementop();
		Boolean elOpNotNull = elementOp != null;

		if (left == null && right == null) {
			return;
		}
		
		if ((left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			iterateThroughSubtrees(smg, left, null, nextNode);
			prevNode = getLastNode(smg);

			iterateThroughSubtrees(smg, right, prevNode, nextNode);
		} else {
			String orderop = order.getOrderop();
			if ((left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
				iterateThroughSubtrees(smg, left, prevNode, nextNode);
				handleOp(smg, orderop, right, prevNode, nextNode);
			} else if (!(left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
				if (orderop.equals("|")) {
					prevNode = getLastNode(smg);
				}
				handleOp(smg, orderop, left, prevNode, nextNode);
				if (orderop.equals("|")) {
					nextNode = getLastNode(smg);
				}

				if (elOpNotNull && elementOp.equals("+")) {
					StateNode linkBackNode = prevNode;

					iterateThroughSubtrees(smg, right, prevNode, nextNode);

					List<TransitionEdge> transEdges = new ArrayList<TransitionEdge>(smg.getEdges());
					for (TransitionEdge trans : transEdges) {
						if (trans.to().equals(nextNode)) {
							if (trans.from().equals(linkBackNode)) {
								smg.addEdge(new TransitionEdge(trans.getLabel(), trans.to(), trans.to()));
							} else {
								for (TransitionEdge innerTrans : transEdges) {
									if (innerTrans.to().equals(trans.from())) {
										smg.addEdge(new TransitionEdge(innerTrans.getLabel(), trans.to(), trans.from()));
									}
								}
							}
						}
					}
				} else {
					iterateThroughSubtrees(smg, right, prevNode, nextNode);
				}

			} else if (!(left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
				if (orderop != null && orderop.equals("|")) {
					prevNode = getLastNode(smg);
				}
				handleOp(smg, orderop, left, prevNode, null);
				if (orderop.equals("|")) {
					nextNode = getLastNode(smg);
					handleOp(smg, orderop, right, prevNode, nextNode);
				} else {
					handleOp(smg, orderop, right, null, nextNode);
				}
			}
		}
	}

	private StateNode getLastNode(StateMachineGraph smg) {
		List<StateNode> nodes = smg.getNodes();
		return nodes.get(nodes.size() - 1);
	}

	private void handleOp(StateMachineGraph smg, String orderop, Expression leaf, StateNode prevNode, StateNode nextNode) {
		prevNode = (prevNode == null) ? getLastNode(smg) : prevNode;
		if (nextNode == null) {
			nextNode = getNewNode();
			smg.addNode(nextNode);
		}

		List<CryptSLMethod> label = resolveAggregateToMethodeNames(leaf.getOrderEv().get(0));
		smg.addEdge(new TransitionEdge(label, prevNode, nextNode));
		prevNode.setAccepting(false);
		if (leaf.getElementop() != null) {
			if (leaf.getElementop().equals("+")) {
				smg.addEdge(new TransitionEdge(label, nextNode, nextNode));
			} else if (leaf.getElementop().equals("*")) {
				smg.addEdge(new TransitionEdge(label, nextNode, nextNode));
				//handle extra edge in case of *
			} else if (leaf.getElementop().equals("?")) {
//				handle extra edge in case of ?
			}
		}
	}

	private List<CryptSLMethod> resolveAggregateToMethodeNames(Event leaf) {
		if (leaf instanceof Aggregate) {
			Aggregate ev = (Aggregate) leaf;
			return dealWithAggregate(ev);
		} else {
			ArrayList<CryptSLMethod> statements = new ArrayList<CryptSLMethod>();
			statements.add(stringifyMethodSignature(leaf));
			return statements;
		}
	}
	
	private List<CryptSLMethod> dealWithAggregate(Aggregate ev) {
		List<CryptSLMethod> statements = new ArrayList<CryptSLMethod>();
		
		for (Event lab : ev.getLab()) {
			if (lab instanceof Aggregate) {
				statements.addAll(dealWithAggregate((Aggregate) lab));
			} else {
				statements.add(stringifyMethodSignature(lab));
			}
		}
		return statements;
	}

	private CryptSLMethod stringifyMethodSignature(Event lab) {
		Method method = ((SuperType) lab).getMeth();
		
		String qualifiedName = method.getMethName().getQualifiedName();
		if (qualifiedName == null) {
			qualifiedName = ((de.darmstadt.tu.crossing.cryptSL.impl.DomainmodelImpl) (method.eContainer().eContainer())).getJavaType().getQualifiedName();
		}
		qualifiedName = removeSPI(qualifiedName);
		List<Entry<String, String>> pars = new ArrayList<Entry<String, String>>();
		Object returnValue = method.getLeftSide();
		if (returnValue != null && returnValue.getName() != null) {
			ObjectDecl v = ((ObjectDecl) returnValue.eContainer());
			pars.add(new SimpleEntry<String, String>(returnValue.getName(), v.getObjectType().getQualifiedName() + ((v.getArray() != null) ? v.getArray() : "")));
		} else {
			pars.add(new SimpleEntry<String, String>("_", "AnyType"));
		}
		ParList parList = method.getParList();
		if (parList != null) {
			for (Par par : parList.getParameters()) {
				String parValue = "_";
				if (par.getVal() != null && par.getVal().getName() != null) {
					ObjectDecl objectDecl = (ObjectDecl) par.getVal().eContainer();
					parValue = par.getVal().getName();
					String parType = objectDecl.getObjectType().getIdentifier() + ((objectDecl.getArray() != null) ? objectDecl.getArray() : "");
					pars.add(new SimpleEntry<String, String>(parValue, parType));
					
				} else {
					pars.add(new SimpleEntry<String, String>(parValue, "AnyType"));
				}
			}
		}
		List<Boolean> backw = new ArrayList<Boolean>(); 
		for (Entry<String, String> par : pars) {
			boolean backwards = true;
			for (CryptSLPredicate pred : predicates) {
				if (par.getKey().equals(pred.getParameters().get(0))) {
					backwards = false;
					continue;
				}
			}
			backw.add(backwards);
		}
		return new CryptSLMethod(qualifiedName, pars, backw);
	}

	private String removeSPI(String qualifiedName) {
		int spiIndex = qualifiedName.lastIndexOf("Spi");
		int dotIndex = qualifiedName.lastIndexOf(".");
		return (spiIndex == dotIndex - 3) ? qualifiedName.substring(0, spiIndex) + qualifiedName.substring(dotIndex) : qualifiedName;
	}

	private StateNode getNewNode() {
		return new StateNode(String.valueOf(nodeNameCounter++), false, true);
	}

//	private Expression getFirstMethod(Expression order) {
//		Expression cur = (Expression) order;
//		Expression prev = null;
//		while (cur != null) {
//			prev = cur;
//			cur = cur.getLeft();
//		}
//		return prev;
//	}
}