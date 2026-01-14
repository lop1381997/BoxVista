//
//  Home Screen.swift
//  BoxVistaIOS
//
//  Created by pol linger on 6/6/25.
//

import SwiftUI

// Color Extension
extension Color {
    static let bvDarkBackground = Color(red: 0.07, green: 0.08, blue: 0.09) // #121212
    static let bvDarkSurface = Color(red: 0.12, green: 0.14, blue: 0.18) // #1E1E1E
    static let bvPrimaryBlue = Color(red: 0.26, green: 0.52, blue: 0.96) // #4285F4
    static let bvAlertRed = Color(red: 0.92, green: 0.26, blue: 0.21) // #EA4335
    static let bvSuccessGreen = Color(red: 0.20, green: 0.66, blue: 0.33) // #34A853
    static let bvWarningYellow = Color(red: 0.98, green: 0.75, blue: 0.18) // #FBC02D
    static let bvTextGray = Color(red: 0.62, green: 0.62, blue: 0.62)
}

struct HomeView: View {
    @ObservedObject private var vm = HomeViewVM()
    
    var body: some View {
        NavigationStack {
            ZStack {
                Color.bvDarkBackground.ignoresSafeArea()

                VStack(spacing: 0) {
                    // Header
                    HStack {
                        Circle()
                            .fill(Color.bvPrimaryBlue)
                            .frame(width: 40, height: 40)
                            .overlay(Text("A").foregroundColor(.white).fontWeight(.bold))

                        VStack(alignment: .leading) {
                            Text("Good Morning,").font(.caption).foregroundColor(Color.bvTextGray)
                            Text("Alex").font(.title3).fontWeight(.bold).foregroundColor(.white)
                        }

                        Spacer()

                        Image(systemName: "bell.fill")
                            .foregroundColor(.white)
                    }
                    .padding(.horizontal)
                    .padding(.top, 10)

                    // Search Bar
                    HStack {
                        Image(systemName: "magnifyingglass").foregroundColor(Color.bvTextGray)
                        Text("Search Box ID, RFID, or Item...")
                            .foregroundColor(Color.bvTextGray)
                            .font(.subheadline)
                        Spacer()
                        Image(systemName: "qrcode.viewfinder").foregroundColor(Color.bvPrimaryBlue)
                    }
                    .padding()
                    .background(Color.bvDarkSurface)
                    .cornerRadius(12)
                    .padding()

                    // Filter Chips
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 10) {
                            ChipView(text: "All Filters", isSelected: true, color: Color.bvPrimaryBlue, bgColor: Color.bvPrimaryBlue.opacity(0.2))
                            ChipView(text: "Warehouse A", isSelected: false, color: Color.bvTextGray, bgColor: Color.bvDarkSurface)
                            ChipView(text: "Discrepancies", isSelected: false, color: Color.bvWarningYellow, bgColor: Color.bvDarkSurface, icon: "exclamationmark.triangle.fill")
                        }
                        .padding(.horizontal)
                    }
                    .padding(.bottom)

                    // Stats Section
                    HStack(spacing: 12) {
                        StatCard(number: "45", label: "Scanned\nToday", color: Color.bvPrimaryBlue, icon: "qrcode")
                        StatCard(number: "3", label: "Action\nRequired", color: Color.bvAlertRed, icon: "exclamationmark.shield.fill")
                        StatCard(number: "12", label: "Pending\nCheck", color: Color.bvWarningYellow, icon: "ellipsis.circle.fill")
                    }
                    .padding(.horizontal)
                    .padding(.bottom, 24)

                    // Recent Scans
                    HStack {
                        Text("Recent Scans").font(.headline).foregroundColor(.white)
                        Spacer()
                        Text("View All").font(.caption).foregroundColor(Color.bvPrimaryBlue)
                    }
                    .padding(.horizontal)

                    ScrollView {
                        LazyVStack(spacing: 12) {
                            if vm.boxes.isEmpty {
                                Text("Loading...").foregroundColor(Color.bvTextGray).padding()
                            } else {
                                ForEach(vm.boxes) { box in
                                    NavigationLink(value: box) {
                                        BoxCard(box: box)
                                    }
                                    .buttonStyle(PlainButtonStyle()) // Keeps the custom card style
                                }
                            }
                        }
                        .padding()
                    }

                    // Bottom Tab Bar
                    HStack {
                        TabBarItem(icon: "square.grid.2x2.fill", text: "Home", isSelected: true, color: Color.bvPrimaryBlue)
                        TabBarItem(icon: "archivebox.fill", text: "Inventory", isSelected: false, color: Color.bvTextGray)

                        ZStack {
                            Circle().fill(Color.bvPrimaryBlue).frame(width: 50, height: 50)
                            Image(systemName: "qrcode.viewfinder").foregroundColor(.white).font(.title2)
                        }
                        .offset(y: -20)

                        TabBarItem(icon: "exclamationmark.triangle.fill", text: "Alerts", isSelected: false, color: Color.bvTextGray)
                        TabBarItem(icon: "gearshape.fill", text: "Settings", isSelected: false, color: Color.bvTextGray)
                    }
                    .padding(.horizontal)
                    .background(Color.bvDarkBackground)
                }
            }
            .navigationDestination(for: Box.self) { box in
                BoxDetailView(box: box)
            }
            .task {
                await vm.load()
            }
            .preferredColorScheme(.dark)
        }
    }
}

struct ChipView: View {
    let text: String
    let isSelected: Bool
    let color: Color
    let bgColor: Color
    var icon: String? = nil

    var body: some View {
        HStack {
            if let icon = icon {
                Image(systemName: icon).foregroundColor(color).font(.caption)
            }
            Text(text).font(.caption).fontWeight(.medium).foregroundColor(color)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
        .background(bgColor)
        .cornerRadius(20)
        .overlay(
            RoundedRectangle(cornerRadius: 20)
                .stroke(isSelected ? color : Color.clear, lineWidth: 1)
        )
    }
}

struct StatCard: View {
    let number: String
    let label: String
    let color: Color
    let icon: String

    var body: some View {
        VStack(alignment: .leading) {
            HStack {
                Text(number).font(.title2).fontWeight(.bold).foregroundColor(.white)
                Spacer()
                Image(systemName: icon).foregroundColor(color)
            }
            Spacer()
            Text(label).font(.caption).foregroundColor(.gray).fixedSize(horizontal: false, vertical: true)
        }
        .padding()
        .frame(height: 100)
        .background(Color.bvDarkSurface)
        .cornerRadius(12)
    }
}

struct BoxCard: View {
    let box: Box

    var body: some View {
        // Mock Data
        let isMismatch = (box.id ?? 0) % 2 == 0
        let statusText = isMismatch ? "MISMATCH" : "VERIFIED"
        let statusColor = isMismatch ? Color.bvAlertRed : Color.bvSuccessGreen
        let location = "Zone A - Shelf 2"
        let time = "10:15 AM"

        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Text("#BX-\(box.id ?? 0)")
                    .font(.headline)
                    .foregroundColor(.white)
                Spacer()
                Text(time).font(.caption).foregroundColor(Color.bvTextGray)
            }

            HStack {
                Image(systemName: "location.fill").foregroundColor(Color.bvTextGray).font(.caption)
                Text(location).font(.caption).foregroundColor(Color.bvTextGray)
            }

            HStack {
                HStack {
                    Rectangle().fill(Color.gray.opacity(0.3)).frame(width: 24, height: 24).cornerRadius(4)
                    Text(box.name).font(.caption).foregroundColor(Color.bvTextGray)
                }
                Spacer()
                HStack {
                    if isMismatch {
                        Image(systemName: "exclamationmark.circle.fill").foregroundColor(statusColor).font(.caption)
                    } else {
                        Image(systemName: "checkmark.circle.fill").foregroundColor(statusColor).font(.caption)
                    }
                    Text(statusText)
                        .font(.caption)
                        .fontWeight(.bold)
                        .foregroundColor(statusColor)
                }
                .padding(.horizontal, 8)
                .padding(.vertical, 4)
                .background(statusColor.opacity(0.2))
                .cornerRadius(4)
            }
        }
        .padding()
        .background(Color.bvDarkSurface)
        .cornerRadius(12)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(isMismatch ? Color.bvAlertRed : Color.clear, lineWidth: 1) // Highlight mismatch
                .padding(1)
                .mask(
                     Rectangle().padding(.leading, -10) // Mock partial border
                )
        )
        // Simple overlay for the colored bar on the left
        .overlay(
            HStack {
                Rectangle()
                    .fill(isMismatch ? Color.bvAlertRed : Color.bvSuccessGreen)
                    .frame(width: 4)
                    .cornerRadius(2)
                Spacer()
            }
        )
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}

struct TabBarItem: View {
    let icon: String
    let text: String
    let isSelected: Bool
    let color: Color

    var body: some View {
        VStack {
            Image(systemName: icon)
                .font(.system(size: 20))
                .foregroundColor(color)
            Text(text)
                .font(.caption2)
                .foregroundColor(color)
        }
        .frame(maxWidth: .infinity)
    }
}

#Preview {
    HomeView()
}
