package smc.implementers;

import smc.Utilities;
import smc.generators.nestedSwitchCaseGenerator.NSCNode;
import smc.generators.nestedSwitchCaseGenerator.NSCNodeVisitor;

import java.util.Map;

public class SwiftNestedSwitchCaseImplementer implements NSCNodeVisitor {
  private String output = "";
  private Map<String, String> flags;
  private String javaPackage = null;

  public SwiftNestedSwitchCaseImplementer(Map<String, String> flags) {
    this.flags = flags;
    if (flags.containsKey("package"))
      javaPackage = flags.get("package");
  }

  public void visit(NSCNode.SwitchCaseNode switchCaseNode) {
    output += String.format("switch(%s) {\n", switchCaseNode.variableName);
    switchCaseNode.generateCases(this);
    output += "}\n";
  }

  public void visit(NSCNode.CaseNode caseNode) {
    output += String.format("case .%s:\n", caseNode.caseName);
    caseNode.caseActionNode.accept(this);
    output += "break\n";
  }

  public void visit(NSCNode.FunctionCallNode functionCallNode) {
    output += String.format("%s(", functionCallNode.functionName);
    if (functionCallNode.argument != null)
      functionCallNode.argument.accept(this);
    output += ")\n";
  }

  public void visit(NSCNode.EnumNode enumNode) {
    output += String.format("enum %s {\n%s}\n", enumNode.name, Utilities.enumList(enumNode.enumerators));

  }

  public void visit(NSCNode.StatePropertyNode statePropertyNode) {
    output += String.format("private var state: State = State.%s\n", statePropertyNode.initialState);
    output += "private func setState(_ s: State) {state = s}\n";
  }

  public void visit(NSCNode.EventDelegatorsNode eventDelegatorsNode) {
    for (String event : eventDelegatorsNode.events)
      output += String.format("func %s() {handleEvent(Event.%s)}\n", event, event);
  }

  public void visit(NSCNode.FSMClassNode fsmClassNode) {
    if (javaPackage != null)
      output += "package " + javaPackage + "\n";

    String actionsName = fsmClassNode.actionsName;
    if (actionsName == null)
      output += String.format("class %s {\n", fsmClassNode.className);
    else
      output += String.format("class %s : %s {\n", fsmClassNode.className, actionsName);

    output += "func unhandledTransition(_ state: String, _ event: String) {}\n";
    fsmClassNode.stateEnum.accept(this);
    fsmClassNode.eventEnum.accept(this);
    fsmClassNode.stateProperty.accept(this);
    fsmClassNode.delegators.accept(this);
    fsmClassNode.handleEvent.accept(this);
    if (actionsName == null) {
      for (String action : fsmClassNode.actions)
        output += String.format("func %s() {}\n", action);
    }
    output += "}\n";
  }

  public void visit(NSCNode.HandleEventNode handleEventNode) {
    output += "private func handleEvent(_ event: Event) {\n";
    handleEventNode.switchCase.accept(this);
    output += "}\n";
  }

  public void visit(NSCNode.EnumeratorNode enumeratorNode) {
    output += String.format("%s.%s", enumeratorNode.enumeration, enumeratorNode.enumerator);
  }

  public void visit(NSCNode.DefaultCaseNode defaultCaseNode) {
    output += "default: unhandledTransition(String(describing:state), String(describing:event))\n";
  }

  public String getOutput() {
    return output;
  }
}
