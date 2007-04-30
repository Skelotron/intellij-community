package com.intellij.lang.ant.psi.impl;

import com.intellij.lang.ant.AntSupport;
import com.intellij.lang.ant.psi.AntElement;
import com.intellij.lang.ant.psi.AntElementVisitor;
import com.intellij.lang.ant.psi.AntFile;
import com.intellij.lang.ant.psi.AntImport;
import com.intellij.lang.ant.psi.introspection.AntTypeDefinition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLock;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.StringBuilderSpinAllocator;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AntImportImpl extends AntTaskImpl implements AntImport {

  public AntImportImpl(final AntElement parent, final XmlTag sourceElement, final AntTypeDefinition definition) {
    super(parent, sourceElement, definition);
  }

  public void acceptAntElementVisitor(@NotNull final AntElementVisitor visitor) {
    visitor.visitAntImport(this);
  }

  public String toString() {
    @NonNls final StringBuilder builder = StringBuilderSpinAllocator.alloc();
    try {
      builder.append("AntImport[");
      builder.append(getSourceElement().getAttributeValue(AntFileImpl.FILE_ATTR));
      builder.append("]");
      return builder.toString();
    }
    finally {
      StringBuilderSpinAllocator.dispose(builder);
    }
  }

  @NotNull
  public List<String> getFileReferenceAttributes() {
    return Collections.singletonList(AntFileImpl.FILE_ATTR);
  }

  @Nullable
  public String getFileName() {
    return computeAttributeValue(getSourceElement().getAttributeValue(AntFileImpl.FILE_ATTR));
  }

  public AntFile getImportedFile() {
    return getImportedFile(getFileName(), this);
  }

  public void clearCaches() {
    synchronized (PsiLock.LOCK) {
      super.clearCaches();
      getAntFile().clearCaches();
    }
  }

  @Nullable
  static AntFile getImportedFile(final String name, final AntStructuredElementImpl element) {
    if (name == null) return null;
    final PsiFile psiFile = element.findFileByName(name);
    if (psiFile != null) {
      if (psiFile instanceof XmlFile) {
        final VirtualFile file = psiFile.getVirtualFile();
        if (file != null) {
          AntSupport.markFileAsAntFile(file, psiFile.getViewProvider(), true);
        }
        return AntSupport.getAntFile(psiFile);
      }
      if (psiFile instanceof AntFile) return (AntFile)psiFile;
    }
    return null;
  }
}
