
folder_name=embed-cdt-initial-cq-v2
rm -rf ~/tmp/${folder_name}
mkdir -p ~/tmp/${folder_name}
cd ~/tmp/${folder_name}

cp -r ~/tmp/eclipse-plugins.git/* .

find . -name '*.jar' -exec rm -v {} \;
echo
for dir in $(find . -name templates -depth 3)
do
  echo
  echo $dir
  ls -l $dir
  rm -rf $dir
done

# NOTE: should have removed the semver sources from
# plugins/ilg.gnumcueclipse.core/src/com/github/zafarkhaja/semver

cd ~/tmp
tar czf ${folder_name}.tgz ${folder_name}
