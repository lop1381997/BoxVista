//
//  NetworkManager.swift
//  BoxVistaIOS
//
//  Created by Pol Linger on 21/6/25.
//

import Foundation

enum APIError: Error {
    case invalidURL
    case invalidResponse
    case decodingError
    case serverError(Int)
}

class NetworkManager {
    static let shared = NetworkManager()
    private let baseURL = URL(string: "http://localhost:3000/api")!
    private let decoder: JSONDecoder = {
        let d = JSONDecoder()
        d.keyDecodingStrategy = .convertFromSnakeCase
        return d
    }()
    private let encoder: JSONEncoder = {
        let e = JSONEncoder()
        e.keyEncodingStrategy = .convertToSnakeCase
        return e
    }()

    // MARK: - Boxes CRUD

    /// Fetch all boxes using DTO mapping
    func fetchBoxes(completion: @escaping (Result<[Box], APIError>) -> Void) {
        let url = baseURL.appendingPathComponent("boxes")
        URLSession.shared.dataTask(with: url) { data, resp, err in
            if let code = (resp as? HTTPURLResponse)?.statusCode, code >= 400 {
                return completion(.failure(.serverError(code)))
            }
            guard let data = data else {
                return completion(.failure(.invalidResponse))
            }
            do {
                // Decode array of DTOs and map to domain
                let dtos = try self.decoder.decode([BoxDTO].self, from: data)
                let boxes = dtos.map { $0.toBox() }
                completion(.success(boxes))
            } catch {
                completion(.failure(.decodingError))
            }
        }.resume()
        print("Fetching boxes...")
    }

    /// Fetch a specific box by id
    func fetchBox(id: Int64, completion: @escaping (Result<Box, APIError>) -> Void) {
        let url = baseURL.appendingPathComponent("boxes/\(id)")
        URLSession.shared.dataTask(with: url) { data, resp, err in
            if let code = (resp as? HTTPURLResponse)?.statusCode, code >= 400 {
                return completion(.failure(.serverError(code)))
            }
            guard let data = data else {
                return completion(.failure(.invalidResponse))
            }
            do {
                let dto = try self.decoder.decode(BoxDTO.self, from: data)
                completion(.success(dto.toBox()))
            } catch {
                completion(.failure(.decodingError))
            }
        }.resume()
    }

    /// Create a new box
    func createBox(name: String,
                   description: String,
                   objects: [ObjectItem],
                   completion: @escaping (Result<Box, APIError>) -> Void) {
        let url = baseURL.appendingPathComponent("boxes")
        var req = URLRequest(url: url)
        req.httpMethod = "POST"
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")

        // Build request payload from domain objects
        let payload = [
            "name": name,
            "description": description,
            "objetos": objects.map { [
                "nombre": $0.nombre,
                "state": $0.state
            ] }
        ] as [String : Any]

        do {
            req.httpBody = try JSONSerialization.data(withJSONObject: payload, options: [])
        } catch {
            return completion(.failure(.decodingError))
        }

        URLSession.shared.dataTask(with: req) { data, resp, err in
            if let code = (resp as? HTTPURLResponse)?.statusCode, code >= 400 {
                return completion(.failure(.serverError(code)))
            }
            guard let data = data else {
                return completion(.failure(.invalidResponse))
            }
            do {
                let dto = try self.decoder.decode(BoxDTO.self, from: data)
                completion(.success(dto.toBox()))
            } catch {
                completion(.failure(.decodingError))
            }
        }.resume()
    }

    /// Update an existing box
    func updateBox(_ box: Box,
                   completion: @escaping (Result<Box, APIError>) -> Void) {
        let url = baseURL.appendingPathComponent("boxes/\(box.id)")
        var req = URLRequest(url: url)
        req.httpMethod = "PUT"
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")

        // Build payload similarly to create
        let payload = [
            "name": box.name,
            "description": box.description,
            "objetos": box.objects.map { [
                "nombre": $0.nombre,
                "state": $0.state
            ] }
        ] as [String : Any]

        do {
            req.httpBody = try JSONSerialization.data(withJSONObject: payload, options: [])
        } catch {
            return completion(.failure(.decodingError))
        }

        URLSession.shared.dataTask(with: req) { data, resp, err in
            if let code = (resp as? HTTPURLResponse)?.statusCode, code >= 400 {
                return completion(.failure(.serverError(code)))
            }
            guard let data = data else {
                return completion(.failure(.invalidResponse))
            }
            do {
                let dto = try self.decoder.decode(BoxDTO.self, from: data)
                completion(.success(dto.toBox()))
            } catch {
                completion(.failure(.decodingError))
            }
        }.resume()
    }

    /// Delete a box
    func deleteBox(id: Int64, completion: @escaping (Result<Void, APIError>) -> Void) {
        let url = baseURL.appendingPathComponent("boxes/\(id)")
        var req = URLRequest(url: url)
        req.httpMethod = "DELETE"
        URLSession.shared.dataTask(with: req) { _, resp, _ in
            if let code = (resp as? HTTPURLResponse)?.statusCode, code >= 400 {
                return completion(.failure(.serverError(code)))
            }
            completion(.success(()))
        }.resume()
    }

    // MARK: - ObjectItems CRUD

    /// Fetch all objects for a box
    func fetchObjects(for boxId: Int64,
                      completion: @escaping (Result<[ObjectItem], APIError>) -> Void) {
        let url = baseURL.appendingPathComponent("boxes/\(boxId)/objects")
        URLSession.shared.dataTask(with: url) { data, resp, err in
            if let code = (resp as? HTTPURLResponse)?.statusCode, code >= 400 {
                return completion(.failure(.serverError(code)))
            }
            guard let data = data else {
                return completion(.failure(.invalidResponse))
            }
            do {
                let dtos = try self.decoder.decode([ObjectDTO].self, from: data)
                let items = dtos.map { $0.toObjectItem() }
                completion(.success(items))
            } catch {
                completion(.failure(.decodingError))
            }
        }.resume()
    }

    /// Create a new object in a box
    func createObject(_ obj: ObjectItem,
                      in boxId: Int64,
                      completion: @escaping (Result<ObjectItem, APIError>) -> Void) {
        let url = baseURL.appendingPathComponent("boxes/\(boxId)/objects")
        var req = URLRequest(url: url)
        req.httpMethod = "POST"
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")

        let payload = [
            "nombre": obj.nombre,
            "state": obj.state
        ] as [String : Any]

        do {
            req.httpBody = try JSONSerialization.data(withJSONObject: payload, options: [])
        } catch {
            return completion(.failure(.decodingError))
        }

        URLSession.shared.dataTask(with: req) { data, resp, err in
            if let code = (resp as? HTTPURLResponse)?.statusCode, code >= 400 {
                return completion(.failure(.serverError(code)))
            }
            guard let data = data else {
                return completion(.failure(.invalidResponse))
            }
            do {
                let dto = try self.decoder.decode(ObjectDTO.self, from: data)
                completion(.success(dto.toObjectItem()))
            } catch {
                completion(.failure(.decodingError))
            }
        }.resume()
    }

    /// Update an object in a box
    func updateObject(_ obj: ObjectItem,
                      in boxId: Int64,
                      completion: @escaping (Result<ObjectItem, APIError>) -> Void) {
        let url = baseURL.appendingPathComponent("boxes/\(boxId)/objects/\(obj.id)")
        var req = URLRequest(url: url)
        req.httpMethod = "PUT"
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")

        let payload = [
            "nombre": obj.nombre,
            "state": obj.state
        ] as [String : Any]

        do {
            req.httpBody = try JSONSerialization.data(withJSONObject: payload, options: [])
        } catch {
            return completion(.failure(.decodingError))
        }

        URLSession.shared.dataTask(with: req) { data, resp, err in
            if let code = (resp as? HTTPURLResponse)?.statusCode, code >= 400 {
                return completion(.failure(.serverError(code)))
            }
            guard let data = data else {
                return completion(.failure(.invalidResponse))
            }
            do {
                let dto = try self.decoder.decode(ObjectDTO.self, from: data)
                completion(.success(dto.toObjectItem()))
            } catch {
                completion(.failure(.decodingError))
            }
        }.resume()
    }

    /// Delete an object from a box
    func deleteObject(id: Int64,
                      in boxId: Int64,
                      completion: @escaping (Result<Void, APIError>) -> Void) {
        let url = baseURL.appendingPathComponent("boxes/\(boxId)/objects/\(id)")
        var req = URLRequest(url: url)
        req.httpMethod = "DELETE"
        URLSession.shared.dataTask(with: req) { _, resp, _ in
            if let code = (resp as? HTTPURLResponse)?.statusCode, code >= 400 {
                return completion(.failure(.serverError(code)))
            }
            completion(.success(()))
        }.resume()
    }
}
