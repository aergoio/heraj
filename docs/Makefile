# Minimal makefile for Sphinx documentation
#

# You can set these variables from the command line, and also
# from the environment for the first two.
SPHINXOPTS    ?=
SPHINXBUILD   ?= sphinx-build
SPHINXINTL    ?= sphinx-intl
SOURCEDIR     = source
BUILDDIR      = build

# Put it first so that "make" without argument is like "make help".
help:
	@$(SPHINXBUILD) -M help "$(SOURCEDIR)" "$(BUILDDIR)" $(SPHINXOPTS) $(O)

# Catch-all target: route all unknown targets to Sphinx using the new
# "make mode" option.  $(O) is meant as a shortcut for $(SPHINXOPTS).
%: Makefile
	@$(SPHINXBUILD) -M $@ "$(SOURCEDIR)" "$(BUILDDIR)" $(SPHINXOPTS) $(O)

.PHONY: help Makefile all en ko clean


# Custom targets

all: en ko

locale: gettext
	@$(SPHINXINTL) update -p "$(BUILDDIR)/gettext" -l ko

en:
	@$(SPHINXBUILD) "$(SOURCEDIR)" "$(BUILDDIR)/html/en" -D language=en $(SPHINXOPTS) $(O)

ko:
	@$(SPHINXBUILD) "$(SOURCEDIR)" "$(BUILDDIR)/html/ko" -D language=ko $(SPHINXOPTS) $(O)

clean:
	@rm -rf "$(BUILDDIR)"
