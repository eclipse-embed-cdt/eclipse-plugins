
folder_name=embed-cdt-template-ilg-cq
rm -rf ~/tmp/${folder_name}
mkdir -p ~/tmp/${folder_name}
cd ~/tmp/${folder_name}

mkdir -p plugins/ilg.gnumcueclipse.templates.core
cp -r ~/tmp/eclipse-plugins.git/plugins/ilg.gnumcueclipse.templates.core/templates plugins/ilg.gnumcueclipse.templates.core

mkdir -p plugins/ilg.gnumcueclipse.templates.sifive
cp -r ~/tmp/eclipse-plugins.git/plugins/ilg.gnumcueclipse.templates.sifive/templates plugins/ilg.gnumcueclipse.templates.sifive

mkdir -p plugins/ilg.gnumcueclipse.templates.freescale.pe
cp -r ~/tmp/eclipse-plugins.git/plugins/ilg.gnumcueclipse.templates.freescale.pe/templates plugins/ilg.gnumcueclipse.templates.freescale.pe

mkdir -p plugins/ilg.gnumcueclipse.managedbuild.cross.riscv
cp -r ~/tmp/eclipse-plugins.git/plugins/ilg.gnumcueclipse.managedbuild.cross.riscv/templates plugins/ilg.gnumcueclipse.managedbuild.cross.riscv

mkdir -p plugins/ilg.gnumcueclipse.managedbuild.cross.arm
cp -r ~/tmp/eclipse-plugins.git/plugins/ilg.gnumcueclipse.managedbuild.cross.arm/templates plugins/ilg.gnumcueclipse.managedbuild.cross.arm


cd ~/tmp
tar czf ${folder_name}.tgz ${folder_name}
