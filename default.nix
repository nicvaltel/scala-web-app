{ nixpkgs ? import <nixpkgs> {  } }:
let
  nixpkgsRev = "ea5234e7073d5f44728c499192544a84244bf35a";
  nixpkgsSha = "sha256:1iqfglh1fdgqxm7n4763k1cipna68sa0cb3azm2gdzhr374avcvk";
  compiler = pkgs.haskellPackages;
  security.sudo.enable = true;
  pkgs = import (builtins.fetchTarball {
    url = "https://github.com/nixos/nixpkgs/archive/${nixpkgsRev}.tar.gz";
    sha256 = nixpkgsSha;
  }) {} ;

in
  pkgs.stdenv.mkDerivation {
    name = "env";
    buildInputs =  [
      pkgs.tree # show files in directory as tree

      pkgs.scala_3
			pkgs.sbt
    ];

     shellHook = ''
         echo "Entering my Nix shell environment..."
     '';
}
