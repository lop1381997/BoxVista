//
//  NetworkManager.swift
//  BoxVistaIOS
//
//  Created by Pol Linger on 21/6/25.
//

import Foundation

enum APIError: Error, LocalizedError {
    case invalidURL
    case invalidResponse
    case decodingError
    case unauthorized(String?)
    case serverError(Int, String?)

    var errorDescription: String? {
        switch self {
        case .invalidURL:
            return "URL inválida"
        case .invalidResponse:
            return "Respuesta inválida del servidor"
        case .decodingError:
            return "No se pudo procesar la respuesta del servidor"
        case .unauthorized(let message):
            return message ?? "Sesión no iniciada o caducada"
        case .serverError(let code, let message):
            return message ?? "Error del servidor (código \(code))"
        }
    }
}

private struct AuthRequest: Encodable {
    let email: String
    let password: String
}

private struct AuthResponse: Decodable {
    let token: String
}

private struct APIErrorResponse: Decodable {
    let message: String?
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

    // MARK: - Auth

    func login(email: String,
               password: String,
               completion: @escaping (Result<String, APIError>) -> Void) {
        authenticate(path: "auth/login", email: email, password: password, completion: completion)
    }

    func register(email: String,
                  password: String,
                  completion: @escaping (Result<String, APIError>) -> Void) {
        authenticate(path: "auth/register", email: email, password: password, completion: completion)
    }

    func login(email: String, password: String) async throws -> String {
        try await withCheckedThrowingContinuation { continuation in
            login(email: email, password: password) { result in
                switch result {
                case .success(let token):
                    continuation.resume(returning: token)
                case .failure(let error):
                    continuation.resume(throwing: error)
                }
            }
        }
    }

    func register(email: String, password: String) async throws -> String {
        try await withCheckedThrowingContinuation { continuation in
            register(email: email, password: password) { result in
                switch result {
                case .success(let token):
                    continuation.resume(returning: token)
                case .failure(let error):
                    continuation.resume(throwing: error)
                }
            }
        }
    }

    private func authenticate(path: String,
                              email: String,
                              password: String,
                              completion: @escaping (Result<String, APIError>) -> Void) {
        let url = baseURL.appendingPathComponent(path)
        var req = URLRequest(url: url)
        req.httpMethod = "POST"
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")

        do {
            req.httpBody = try encoder.encode(AuthRequest(email: email, password: password))
        } catch {
            return completion(.failure(.decodingError))
        }

        URLSession.shared.dataTask(with: req) { data, resp, err in
            if err != nil {
                return completion(.failure(.invalidResponse))
            }
            if let error = self.apiError(from: data, response: resp) {
                return completion(.failure(error))
            }
            guard let data = data else {
                return completion(.failure(.invalidResponse))
            }
            do {
                let authResponse = try self.decoder.decode(AuthResponse.self, from: data)
                AuthTokenStore.shared.token = authResponse.token
                completion(.success(authResponse.token))
            } catch {
                completion(.failure(.decodingError))
            }
        }.resume()
    }

    // MARK: - Boxes CRUD

    /// Fetch all boxes using DTO mapping
    func fetchBoxes(completion: @escaping (Result<[Box], APIError>) -> Void) {
        let url = baseURL.appendingPathComponent("boxes")
        var req = URLRequest(url: url)
        addAuthHeader(to: &req)

        URLSession.shared.dataTask(with: req) { data, resp, err in
            if err != nil {
                return completion(.failure(.invalidResponse))
            }
            if let error = self.apiError(from: data, response: resp) {
                return completion(.failure(error))
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
    }

    /// Fetch a specific box by id
    func fetchBox(id: Int64, completion: @escaping (Result<Box, APIError>) -> Void) {
        let url = baseURL.appendingPathComponent("boxes/\(id)")
        var req = URLRequest(url: url)
        addAuthHeader(to: &req)

        URLSession.shared.dataTask(with: req) { data, resp, err in
            if err != nil {
                return completion(.failure(.invalidResponse))
            }
            if let error = self.apiError(from: data, response: resp) {
                return completion(.failure(error))
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
        addAuthHeader(to: &req)

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
            if err != nil {
                return completion(.failure(.invalidResponse))
            }
            if let error = self.apiError(from: data, response: resp) {
                return completion(.failure(error))
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
        addAuthHeader(to: &req)

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
            if err != nil {
                return completion(.failure(.invalidResponse))
            }
            if let error = self.apiError(from: data, response: resp) {
                return completion(.failure(error))
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
        addAuthHeader(to: &req)
        URLSession.shared.dataTask(with: req) { data, resp, err in
            if err != nil {
                return completion(.failure(.invalidResponse))
            }
            if let error = self.apiError(from: data, response: resp) {
                return completion(.failure(error))
            }
            completion(.success(()))
        }.resume()
    }

    // MARK: - ObjectItems CRUD

    /// Fetch all objects for a box
    func fetchObjects(for boxId: Int64,
                      completion: @escaping (Result<[ObjectItem], APIError>) -> Void) {
        let url = baseURL.appendingPathComponent("boxes/\(boxId)/objects")
        var req = URLRequest(url: url)
        addAuthHeader(to: &req)

        URLSession.shared.dataTask(with: req) { data, resp, err in
            if err != nil {
                return completion(.failure(.invalidResponse))
            }
            if let error = self.apiError(from: data, response: resp) {
                return completion(.failure(error))
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
        addAuthHeader(to: &req)

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
            if err != nil {
                return completion(.failure(.invalidResponse))
            }
            if let error = self.apiError(from: data, response: resp) {
                return completion(.failure(error))
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
        addAuthHeader(to: &req)

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
            if err != nil {
                return completion(.failure(.invalidResponse))
            }
            if let error = self.apiError(from: data, response: resp) {
                return completion(.failure(error))
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
        addAuthHeader(to: &req)
        URLSession.shared.dataTask(with: req) { data, resp, err in
            if err != nil {
                return completion(.failure(.invalidResponse))
            }
            if let error = self.apiError(from: data, response: resp) {
                return completion(.failure(error))
            }
            completion(.success(()))
        }.resume()
    }

    private func addAuthHeader(to request: inout URLRequest) {
        guard let token = AuthTokenStore.shared.token, !token.isEmpty else {
            return
        }
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
    }

    private func apiError(from data: Data?, response: URLResponse?) -> APIError? {
        guard let code = (response as? HTTPURLResponse)?.statusCode, code >= 400 else {
            return nil
        }

        let message = responseMessage(from: data)
        if code == 401 {
            return .unauthorized(message)
        }
        return .serverError(code, message)
    }

    private func responseMessage(from data: Data?) -> String? {
        guard let data, !data.isEmpty else { return nil }

        if let errorResponse = try? decoder.decode(APIErrorResponse.self, from: data),
           let message = errorResponse.message,
           !message.isEmpty {
            return message
        }

        return String(data: data, encoding: .utf8)
    }
}
