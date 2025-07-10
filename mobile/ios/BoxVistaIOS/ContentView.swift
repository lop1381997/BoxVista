//
//  ContentView.swift
//  BoxVistaIOS
//
//  Created by pol linger on 6/6/25.
//

import SwiftUI

struct Item: Identifiable {
    let id = UUID()
    var name: String
    var isValid: Bool = false
}
import UIKit

struct ContentView: View {
    var body: some View {
        TabView {
            HomeView()
                .tabItem {
                    Label("Home", systemImage: "house")
                }
            
            SettingsView()
                .tabItem {
                    Label("Settings", systemImage: "gear")
                }
            
            AddBox()
                .tabItem {
                    Label("Add Box", systemImage: "plus")
                }
            
        }
    }
}





#Preview {
    ContentView()
}
