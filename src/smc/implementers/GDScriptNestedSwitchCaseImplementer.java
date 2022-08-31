package smc.implementers;

import smc.Utilities;
import smc.generators.nestedSwitchCaseGenerator.NSCNode;
import smc.generators.nestedSwitchCaseGenerator.NSCNodeVisitor;

import java.util.Map;

public class GDScriptNestedSwitchCaseImplementer implements NSCNodeVisitor {
  private String output = "";
  private Map<String, String> flags;
  private String javaPackage = null;
    int currentMatchIndent = 0;
    
  public GDScriptNestedSwitchCaseImplementer(Map<String, String> flags) {
    this.flags = flags;
    if (flags.containsKey("package"))
      javaPackage = flags.get("package");
  }

  public void visit(NSCNode.SwitchCaseNode switchCaseNode) {
    indentForNesting();
    currentMatchIndent = lastLineSpaceCount();
    output += String.format("match %s:\n", switchCaseNode.variableName);
    switchCaseNode.generateCases(this);
    currentMatchIndent -= 8;
  }
  
  public void visit(NSCNode.CaseNode caseNode) {
      addSpaces(currentMatchIndent);
    output += String.format("%s:\n", caseNode.switchName + "." + caseNode.caseName);
    caseNode.caseActionNode.accept(this);
  }

  public void visit(NSCNode.FunctionCallNode functionCallNode) {
      addSpaces(currentMatchIndent + 4);
    output += String.format("%s(", functionCallNode.functionName);
    if (functionCallNode.argument != null)
      functionCallNode.argument.accept(this);
    output += ")\n";
  }

  public void visit(NSCNode.EnumNode enumNode) {
    output += String.format("enum %s {%s}\n", enumNode.name, Utilities.commaList(enumNode.enumerators));

  }

  public void visit(NSCNode.StatePropertyNode statePropertyNode) {
    output += String.format("var state = State.%s\n", statePropertyNode.initialState);
    output += "func setState(s):\n    state = s\n";
  }

  public void visit(NSCNode.EventDelegatorsNode eventDelegatorsNode) {
    for (String event : eventDelegatorsNode.events)
      output += String.format("func %s():\n    handle_event(Event.%s)\n", event, event);
  }

  public void visit(NSCNode.FSMClassNode fsmClassNode) {
    String actionsName = fsmClassNode.actionsName;
    if (actionsName == null)
      output += "extends Node\n";
    else
      output += String.format("extends %s:\n", actionsName);

    output += "func unhandled_transition(_state, _event):\n    pass\n";
    fsmClassNode.stateEnum.accept(this);
    fsmClassNode.eventEnum.accept(this);
    fsmClassNode.stateProperty.accept(this);
    fsmClassNode.delegators.accept(this);
    fsmClassNode.handleEvent.accept(this);
    if (actionsName == null) {
      for (String action : fsmClassNode.actions)
        output += String.format("func %s():\n    pass\n", action);
    }
  }

  public void visit(NSCNode.HandleEventNode handleEventNode) {
    output += "func handle_event(event):\n";
    handleEventNode.switchCase.accept(this);
  }

  public void visit(NSCNode.EnumeratorNode enumeratorNode) {
    output += String.format("%s.%s", enumeratorNode.enumeration, enumeratorNode.enumerator);
  }

  public void visit(NSCNode.DefaultCaseNode defaultCaseNode) {
    addSpaces(currentMatchIndent);
    output += "_:\n";
    addSpaces(currentMatchIndent + 4);
    output += "unhandled_transition(state, event)\n";
  }

  public String getOutput() {
    return output;
  }

    private int lastLineSpaceCount() {
      String[] arr = output.split("\n");
      String lastLine = arr[arr.length - 1];
      int spaceCount = 4;
      for (char c : lastLine.toCharArray()) {
        if (c == ' ')
	  spaceCount++;
        else
	  break;
      }
      return spaceCount;
    }
    
     private void indentForNesting() {
      String[] arr = output.split("\n");
      String lastLine = arr[arr.length - 1];
      int spaceCount = 4;
      for (char c : lastLine.toCharArray()) {
        if (c == ' ')
	  spaceCount++;
        else
	  break;
      }
      for (int i = 0; i < spaceCount; i++)
        output += " ";
    }

    private void indentSame() {
      String[] arr = output.split("\n");
      String lastLine = arr[arr.length - 1];
      int spaceCount = 0;
      for (char c : lastLine.toCharArray()) {
        if (c == ' ')
	  spaceCount++;
        else
	  break;
      }
      for (int i = 0; i < spaceCount; i++)
        output += " ";
    }

    private void addSpaces(int number) {
      for (int i = 0; i < number; i++)
        output += " ";
    }
}
