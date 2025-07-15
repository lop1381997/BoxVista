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
    var isValid: Bool = true
}
import UIKit

struct ContentView: View {
    @State private var selectedTab = 0
    
    var body: some View {
        TabView(selection: $selectedTab) {
            HomeView()
                .tabItem {
                    Label("Home", systemImage: "house")
                }
                .tag(0)
            
            SettingsView()
                .tabItem {
                    Label("Settings", systemImage: "gear")
                }
                .tag(1)
            
            AddBox(selectedTab: $selectedTab)
                .tabItem {
                    Label("Add Box", systemImage: "plus")
                }
                .tag(2)
        }
    }
}





#Preview {
    ContentView()
}
