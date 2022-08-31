package smc.generators;

import smc.OptimizedStateMachine;
import smc.generators.nestedSwitchCaseGenerator.NSCNodeVisitor;
import smc.implementers.GDScriptNestedSwitchCaseImplementer;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class GDScriptCodeGenerator extends CodeGenerator {
  private GDScriptNestedSwitchCaseImplementer implementer;

  public GDScriptCodeGenerator(OptimizedStateMachine optimizedStateMachine,
                           String outputDirectory,
                           Map<String, String> flags) {
    super(optimizedStateMachine, outputDirectory, flags);
    implementer = new GDScriptNestedSwitchCaseImplementer(flags);
  }

  protected NSCNodeVisitor getImplementer() {
    return implementer;
  }

  public void writeFiles() throws IOException {
    String outputFileName = optimizedStateMachine.header.fsm + ".gd";
    Files.write(getOutputPath(outputFileName), implementer.getOutput().getBytes());
  }
}
