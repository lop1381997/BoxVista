// swift-tools-version: 6.1
import PackageDescription

let package = Package(
    name: "BoxVistaIOS",
    platforms: [ .iOS(.v15) ],
    products: [
        .executable(name: "BoxVistaIOS", targets: ["BoxVistaIOS"])
    ],
    targets: [
        .executableTarget(
            name: "BoxVistaIOS"),
        .testTarget(
            name: "BoxVistaIOSTests",
            dependencies: ["BoxVistaIOS"]),
    ]
)
