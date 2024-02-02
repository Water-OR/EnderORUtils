package io.github.enderor.capabilities;

public interface IArrowCapability extends IEnderORCapability {
  void setDisappearAfterLanded(boolean value);
  
  boolean getDisappearAfterLanded();
  
  void setCanDamageEMan(boolean value);
  
  boolean getCanDamageEMan();
}
