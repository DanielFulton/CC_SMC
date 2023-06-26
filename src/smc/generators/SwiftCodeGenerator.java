package smc.generators;

import smc.OptimizedStateMachine;
import smc.generators.nestedSwitchCaseGenerator.NSCNodeVisitor;
import smc.implementers.SwiftNestedSwitchCaseImplementer;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class SwiftCodeGenerator extends CodeGenerator {
  private SwiftNestedSwitchCaseImplementer implementer;

  public SwiftCodeGenerator(OptimizedStateMachine optimizedStateMachine,
                           String outputDirectory,
                           Map<String, String> flags) {
    super(optimizedStateMachine, outputDirectory, flags);
    implementer = new SwiftNestedSwitchCaseImplementer(flags);
  }

  protected NSCNodeVisitor getImplementer() {
    return implementer;
  }

  public void writeFiles() throws IOException {
    String outputFileName = optimizedStateMachine.header.fsm + ".swift";
    Files.write(getOutputPath(outputFileName), implementer.getOutput().getBytes());
  }
}
