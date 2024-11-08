package com.peninsula.batch.service;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peninsula.batch.model.AssignToBatch;
import com.peninsula.batch.model.BatchDetails;
import com.peninsula.batch.repository.AssignToBatchRepository;
import com.peninsula.batch.repository.BatchDetailsRepository;
import com.peninsula.common.repository.UserCredentialRepository;
import com.peninsula.registration.model.UserCredentials;
import com.peninsula.registration.model.UserPersonalDetails;
import com.peninsula.registration.service.RegistrationService;

@Service
public class BatchService {
	private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

	@Autowired
	private BatchDetailsRepository batchDetailsRepository;

	@Autowired
	private AssignToBatchRepository assignToBatchRepository;

	@Autowired
	private UserCredentialRepository userCredentialRepository;

//	Map<String, Object> response = new HashMap<>();

	// adding batch method
	public Map<String, Object> addBatch(HashMap<String, Object> data, String userToken) {
		Map<String, Object> response = new HashMap<>();
		try {
//			response.clear();
			// checking all parameters coming or not
			@SuppressWarnings("unchecked")
			HashMap<String, Object> parameters = (HashMap<String, Object>) data.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> batch = (HashMap<String, Object>) parameters.get("batch");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> userCredentials = (HashMap<String, Object>) data.get("userCredentials");

			if (batch.get("batchName").toString().isEmpty() || batch.get("batchType") == null
					|| batch.get("batchType").toString().isEmpty() || batch.get("batchStartingDate") == null
					|| batch.get("batchStartingDate").toString().isEmpty() || batch.get("batchEndingDate") == null
					|| batch.get("batchEndingDate").toString().isEmpty() || userCredentials.get("userId") == null
					|| userCredentials.get("userId").toString().isEmpty()) {
				response.put("status", "Error");
				response.put("message", "all parameters required");
				return response;
			}

			// checking the batch name already exist or not
			boolean isBatchNameExist = batchDetailsRepository
					.batchNameExistOrNot(batch.get("batchName").toString().trim());
			UserCredentials user = userCredentialRepository
					.findByUserId(Long.parseLong(userCredentials.get("userId").toString().trim()));
			boolean token = Boolean.parseBoolean(userToken);
			if (user == null) {
				response.put("status", "Error");
				response.put("message", "User doesn't exist");
				return response;
			} else if (!token) {
				response.put("status", "Error");
				response.put("message", "invalid token");
				return response;
			} else if (isBatchNameExist) {
				response.put("status", "Error");
				response.put("message", "Batch name already exist");
				return response;
			} else {
				String startDateStr = batch.get("batchStartingDate").toString().trim();
				String endDateStr = batch.get("batchEndingDate").toString().trim();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				try {// checking the date format
					LocalDate startDate = LocalDate.parse(startDateStr, formatter);
					LocalDate endDate = LocalDate.parse(endDateStr, formatter);
					BatchDetails batchDetails = new BatchDetails();
					batchDetails.setBatchStartingDate(startDate);
					batchDetails.setBatchEndingDate(endDate);
					batchDetails.setBatchName(batch.get("batchName").toString().trim());
					batchDetails.setBatchType(batch.get("batchType").toString().trim());
					batchDetails.setBatchCode(generateBatchCode(batch.get("batchType").toString().trim(),
							batch.get("batchName").toString().trim()));
					batchDetailsRepository.save(batchDetails);
					response.put("batchId", batchDetails.getBatchId());
					response.put("batchCode", batchDetails.getBatchCode());
					response.put("batchName", batchDetails.getBatchName());
					response.put("batchType", batchDetails.getBatchType());
					response.put("batchStartingDate", startDateStr);
					response.put("batchEndingDate", endDateStr);
					List<Map<String, Object>> players = new ArrayList<>();
					List<Map<String, Object>> coaches = new ArrayList<>();
					List<Map<String, Object>> batches = new ArrayList<>();
					if (batch.get("players") != null) {
						batch.put("batchId", batchDetails.getBatchId());
						HashMap<String, Object> assign = new HashMap<>();
						assign.put("userCredentials", userCredentials);
						assign.put("parameters", parameters);
						Map<String, Object> playerResponse = assignPlayerToBatch(assign, userToken);
						if (playerResponse.get("status").toString().equals("Error")) {
							response.put("message", playerResponse.get("message").toString() + " ! "
									+ "batch creation is success but player assigning is failed");
							response.put("status", "Error");
							return response;
						}
						@SuppressWarnings("unchecked")
						List<Map<String, Object>> playerList = (List<Map<String, Object>>) batch.get("players");
						players.addAll(playerList);
						response.put("players", players);
					}
					if (batch.get("coaches") != null) {
						HashMap<String, Object> batchMap = new HashMap<>();
						batchMap.put("batchId", batchDetails.getBatchId());
						batches.add(batchMap);
						parameters.put("batches", batches);
						HashMap<String, Object> assign = new HashMap<>();
						assign.put("userCredentials", userCredentials);
						assign.put("parameters", parameters);
						Map<String, Object> coachResponse = new HashMap<>();
						coachResponse = assignCoachToBatch(assign, userToken);
						if (coachResponse.get("status").toString().equals("Error")) {
							response.put("message", coachResponse.get("message").toString() + "! "
									+ "batch creation is success but coach assigning is failed");
							response.put("status", "Error");
							return response;
						}
						@SuppressWarnings("unchecked")
						List<Map<String, Object>> coachList = (List<Map<String, Object>>) batch.get("coaches");
						coaches.addAll(coachList);
						response.put("coaches", coaches);
					}

					response.put("status", "Success");
					response.put("message", "Batch Successfully cerated");
					return response;
				} catch (DateTimeParseException e) {
					logger.info("Error occured", e);
					response.put("status", "Error");
					response.put("message", "inavlid dateformat, dd-mm-yyyy use this");
					return response;
				}
			}
		} catch (Exception e) {

			logger.info("Error occured", e);
			response.put("status", "Error");
			response.put("message", "Exception error occured");
			return response;
		}

	}

	// player and batch methods--------->

	// assignPlayerToBatch method
	public Map<String, Object> assignPlayerToBatch(HashMap<String, Object> data, String userToken) {
		Map<String, Object> response = new HashMap<>();
		try {
//			response.clear();
			@SuppressWarnings("unchecked")
			HashMap<String, Object> parameters = (HashMap<String, Object>) data.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> batch = (HashMap<String, Object>) parameters.get("batch");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> userCredentials = (HashMap<String, Object>) data.get("userCredentials");
			// checking all parameters coming or not
			if (batch.get("players") == null || batch.get("batchId").toString().isEmpty()
					|| batch.get("batchId") == null || userCredentials.get("userId") == null
					|| userCredentials.get("userId").toString().isEmpty() || userToken == null || userToken.isEmpty()) {
				response.put("status", "Error");
				response.put("message", "all parameters required");
				return response;
			}
			// checking user id and token is valid
			UserCredentials user = userCredentialRepository
					.findByUserId(Long.parseLong(userCredentials.get("userId").toString().trim()));
			boolean token = Boolean.parseBoolean(userToken);
			if (user == null) {
				response.put("status", "Error");
				response.put("message", "User doesn't exist");
				return response;
			} else if (!token) {
				response.put("status", "Error");
				response.put("message", "invalid token");
				return response;
			}

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> players = (List<Map<String, Object>>) batch.get("players");
			// checking all players details valid or not
			for (Map<String, Object> player : players) {
				if (player.get("playerId") == null || player.get("playerId").toString().isEmpty()) {
					response.put("status", "Error");
					response.put("message", "playerId is missing");
					return response;
				} else if (userCredentialRepository
						.getUserIdByPlayerId(Long.parseLong(player.get("playerId").toString().trim())) == null) {
					response.put("status", "Error");
					response.put("message", "Invalid playerId");
					return response;
				} else if (assignToBatchRepository
						.playerAlreadyAssignedOrNot(Long.parseLong(player.get("playerId").toString().trim()))) {
					response.put("status", "Error");
					response.put("message", "player already assigned");
					return response;
				}
			}
			BatchDetails batchDetails = batchDetailsRepository
					.getBatchByBatchId(Long.parseLong(batch.get("batchId").toString().trim()));

			// checking the batch code is valid or not
			if (batchDetails == null) {
				response.put("status", "Error");
				response.put("message", "Invalid batch Id");
				return response;
			} else {
				players.forEach(player -> {
					UserCredentials playerCredetials = userCredentialRepository
							.getUserIdByPlayerId(Long.parseLong(player.get("playerId").toString().trim()));
					// checking the player already assigned to a batch or not
					AssignToBatch assignPlayersToBatch = new AssignToBatch();
					assignPlayersToBatch.setBatchDetails(batchDetails);
					assignPlayersToBatch.setUserCredentials(playerCredetials);
					assignToBatchRepository.save(assignPlayersToBatch);
				});
				response.put("status", "Success");
				response.put("message", "player  assigned successfully ");
				response.put("players", players);
				response.put("batchId", batch.get("batchId").toString().trim());
				return response;
			}

		} catch (Exception e) {
			logger.info("Error occured", e);
			response.put("status", "Error");
			response.put("message", "Excveption error occured");
			return response;
		}
	}

	// get batch of a player method

	public Map<String, Object> getBatchOfaPlayer(HashMap<String, Object> data, String userToken) {
		Map<String, Object> response = new HashMap<>();
		try {
//			response.clear();
			// checking all parameters coming or not
			@SuppressWarnings("unchecked")
			HashMap<String, Object> parameters = (HashMap<String, Object>) data.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> userCredentials = (HashMap<String, Object>) data.get("userCredentials");
			if (parameters.get("player") == null || userCredentials.get("userId") == null
					|| userCredentials.get("userId").toString().isEmpty() || userToken == null || userToken.isEmpty()) {
				response.put("status", "Error");
				response.put("message", "all parameters required");
				return response;

			}
			// checking token and user credentials valid or not
			UserCredentials user = userCredentialRepository
					.findByUserId(Long.parseLong(userCredentials.get("userId").toString().trim()));
			boolean token = Boolean.parseBoolean(userToken);
			if (user == null) {
				response.put("status", "Error");
				response.put("message", "User doesn't exist");
				return response;
			} else if (!token) {
				response.put("status", "Error");
				response.put("message", "invalid token");
				return response;
			}

			// checking player credentials coming or not
			@SuppressWarnings("unchecked")
			HashMap<String, Object> playerDetails = (HashMap<String, Object>) parameters.get("player");
			if (playerDetails.get("playerId") == null || playerDetails.get("playerId").toString().trim().isEmpty()) {
				response.put("status", "Error");
				response.put("message", "all parameters required");
				return response;
			}
			// checking the player id exist
			if (userCredentialRepository
					.getUserIdByPlayerId(Long.parseLong(playerDetails.get("playerId").toString().trim())) == null) {
				response.put("status", "Error");
				response.put("message", "Invalid playerId");
				return response;
			} else {

				// checking the player assigned to a batch or not
				if (!assignToBatchRepository
						.playerAlreadyAssignedOrNot(Long.parseLong(playerDetails.get("playerId").toString().trim()))) {
					response.put("status", "Error");
					response.put("message", "player not assigned to any batch");
					return response;
				} else {
					// getting batch details by player id
					BatchDetails batchDetails = assignToBatchRepository
							.findBatchDetailsByPlayerId(Long.parseLong(playerDetails.get("playerId").toString().trim()));
					response.put("status", "Success");
					response.put("message", "batch details fetched successfully");
					Map<String, Object> responseDetails = new HashMap<>();
					responseDetails.put("batchName", batchDetails.getBatchName());
					responseDetails.put("batchCode", batchDetails.getBatchCode());
					responseDetails.put("batchStartingDate", batchDetails.getBatchStartingDate());
					responseDetails.put("batchEndingDate", batchDetails.getBatchEndingDate());
					response.put("details", responseDetails);

					return response;

				}

			}

		} catch (Exception e) {
			logger.info("Error occured", e);
			response.put("status", "Error");
			response.put("message", "exception error occured");
			return response;
		}

	}

	// get players from batch method
	public Map<String, Object> getPlayersFromaBatch(HashMap<String, Object> data, String userToken) {
		Map<String, Object> response = new HashMap<>();
		try {
			// checking all parameters coming or not
			@SuppressWarnings("unchecked")
			HashMap<String, Object> parameters = (HashMap<String, Object>) data.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> userCredentials = (HashMap<String, Object>) data.get("userCredentials");
			if (parameters.get("batch") == null || userCredentials.get("userId") == null
					|| userCredentials.get("userId").toString().isEmpty() || userToken == null || userToken.isEmpty()) {
				response.put("status", "Error");
				response.put("message", "all parameters required");
				return response;

			}
			// checking token and user credentials valid or not
			UserCredentials user = userCredentialRepository
					.findByUserId(Long.parseLong(userCredentials.get("userId").toString().trim()));
			boolean token = Boolean.parseBoolean(userToken);
			if (user == null) {
				response.put("status", "Error");
				response.put("message", "User doesn't exist");
				return response;
			} else if (!token) {
				response.put("status", "Error");
				response.put("message", "invalid token");
				return response;
			}

			@SuppressWarnings("unchecked")
			HashMap<String, Object> batch = (HashMap<String, Object>) parameters.get("batch");
			// checking batch parameters coming or not
			if (batch.get("batchId") == null || batch.get("batchId").toString().trim().isEmpty()) {
				response.put("status", "Error");
				response.put("message", "all parameters required");
				return response;
			}

			// checking the batch code is valid or not
			if (batchDetailsRepository
					.getBatchByBatchId(Long.parseLong(batch.get("batchId").toString().trim())) == null) {
				response.put("status", "Error");
				response.put("message", "Invalid batch Id");
				return response;
			} else {
				// checking the player assigned to a batch or not
				if (!assignToBatchRepository
						.batchHavePlayersOrNot(Long.parseLong(batch.get("batchId").toString().trim()))) {
					response.put("status", "Error");
					response.put("message", "There is no players assigned to this batch");
					return response;
				} else {
					List<UserPersonalDetails> players = assignToBatchRepository
							.findPlayerDetailsByBatchCode(Long.parseLong(batch.get("batchId").toString().trim()));
					Map<String, Object> responseDetails = new HashMap<>();
					// getting each player
					for (UserPersonalDetails player : players) {
						Map<String, Object> playerData = new HashMap<>();
						playerData.put("playerEmail", player.getEmail());
						playerData.put("playerPhoneNumber", player.getPhoneNumber());

						responseDetails.put(player.getFullName(), playerData);
					}
					response.put("details", responseDetails);
					response.put("status", "Success");
					response.put("message", "players details  fetched successfully ");
					return response;
				}

			}

		} catch (Exception e) {
			logger.info("Error occured", e);
			response.put("status", "Error");
			response.put("message", "exception error occured");
			return response;
		}
	}

	// coach and batch methods------------>

	// assign coach to batch
	public Map<String, Object> assignCoachToBatch(HashMap<String, Object> data, String userToken) {
		Map<String, Object> response = new HashMap<>();
		try {
//			response.clear();
			// checking all parameters getting or not
			@SuppressWarnings("unchecked")
			HashMap<String, Object> parameters = (HashMap<String, Object>) data.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> userCredentials = (HashMap<String, Object>) data.get("userCredentials");
			if (parameters.get("batches") == null || parameters.get("batches").toString().isEmpty()
					|| parameters.get("coaches") == null || userCredentials.get("userId") == null
					|| userCredentials.get("userId").toString().isEmpty() || userToken == null || userToken.isEmpty()) {
				response.put("status", "Error");
				response.put("message", "all parameters required");
				return response;

			}
			// validating user and token
			UserCredentials user = userCredentialRepository
					.findByUserId(Long.parseLong(userCredentials.get("userId").toString().trim()));
			boolean token = Boolean.parseBoolean(userToken);
			if (user == null) {
				response.put("status", "Error");
				response.put("message", "User doesn't exist");
				return response;
			} else if (!token) {
				response.put("status", "Error");
				response.put("message", "invalid token");
				return response;
			}

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> batches = (List<Map<String, Object>>) parameters.get("batches");
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> coaches = (List<Map<String, Object>>) parameters.get("coaches");
			for (Map<String, Object> batch : batches) {
				if (batch.get("batchId") == null || batch.get("batchId").toString().isEmpty()) {
					response.put("status", "Error");
					response.put("message", "batchId is missing");
					return response;
				} else if (batchDetailsRepository
						.getBatchByBatchId(Long.parseLong(batch.get("batchId").toString().trim())) == null) {
					response.put("status", "Error");
					response.put("message", "Invalid coachId");
					return response;
				}

				for (Map<String, Object> coach : coaches) {
					if (coach.get("coachId") == null || coach.get("coachId").toString().isEmpty()) {
						response.put("status", "Error");
						response.put("message", "coachId is missing");
						return response;
					} else if (userCredentialRepository
							.getUserIdByCoachId(Long.parseLong(coach.get("coachId").toString().trim())) == null) {
						response.put("status", "Error");
						response.put("message", "Invalid coachId");
						return response;
					} else if (assignToBatchRepository.coachAlreadyAssignedOrNotToSameBatch(
							Long.parseLong(coach.get("coachId").toString().trim()),
							Long.parseLong(batch.get("batchId").toString().trim()))) {
						response.put("status", "Error");
						response.put("message", "coach already assigned to this batch");
						return response;
					}
				}
			}
			batches.forEach(batch -> {
				BatchDetails batchDetails = batchDetailsRepository
						.getBatchByBatchId(Long.parseLong(batch.get("batchId").toString().trim()));
				coaches.forEach(coach -> {
					UserCredentials coachCredentials = userCredentialRepository
							.getUserIdByCoachId(Long.parseLong(coach.get("coachId").toString().trim()));

					// checking the player already assigned to a batch or not

					AssignToBatch assignCoachToBatch = new AssignToBatch();
					assignCoachToBatch.setBatchDetails(batchDetails);
					assignCoachToBatch.setUserCredentials(coachCredentials);
					assignToBatchRepository.save(assignCoachToBatch);
				});
			});
			response.put("status", "Success");
			response.put("message", "coach  assigned successfully ");
			response.put("coaches", coaches);
			response.put("batches", batches);
			return response;

		} catch (Exception e) {
			logger.info("Error occured", e);
			response.put("status", "Error");
			response.put("message", "exception error occured");
			return response;
		}
	}

	// get batches of a coach method
	public Map<String, Object> getBatchOfaCoach(HashMap<String, Object> data, String userToken) {
		Map<String, Object> response = new HashMap<>();
		try {
//			response.clear();
			// checking all parameters getting or not
			@SuppressWarnings("unchecked")
			HashMap<String, Object> parameters = (HashMap<String, Object>) data.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> userCredentials = (HashMap<String, Object>) data.get("userCredentials");
			if (parameters.get("coach") == null || userCredentials.get("userId") == null
					|| userCredentials.get("userId").toString().isEmpty() || userToken == null || userToken.isEmpty()) {
				response.put("status", "Error");
				response.put("message", "all parameters required");
				return response;

			}
			// validating user and token
			UserCredentials user = userCredentialRepository
					.findByUserId(Long.parseLong(userCredentials.get("userId").toString().trim()));
			boolean token = Boolean.parseBoolean(userToken);
			if (user == null) {
				response.put("status", "Error");
				response.put("message", "User doesn't exist");
				return response;
			} else if (!token) {
				response.put("status", "Error");
				response.put("message", "invalid token");
				return response;
			}
			// checking player credentials coming or not
			@SuppressWarnings("unchecked")
			HashMap<String, Object> coach = (HashMap<String, Object>) parameters.get("coach");
			if (coach.get("coachId") == null || coach.get("coachId").toString().trim().isEmpty()) {
				response.put("status", "Error");
				response.put("message", "all parameters required");
				return response;
			}

			// checking the coach id exist
			if (userCredentialRepository
					.getUserIdByCoachId(Long.parseLong(coach.get("coachId").toString().trim())) == null) {
				response.put("status", "Error");
				response.put("message", "Invalid coachId");
				return response;
			} else {
				// checking the coach assigned to a batch or not
				if (!assignToBatchRepository
						.coachAlreadyAssignedOrNot(Long.parseLong(coach.get("coachId").toString().trim()))) {
					response.put("status", "Error");
					response.put("message", "coach not assigned to any batch");
					return response;
				} else {
					// getting batch details by player id
					List<BatchDetails> batches = assignToBatchRepository
							.findBatchDetailsByCoachId(coach.get("coachId").toString().trim());
					Map<String, Object> responseDetails = new HashMap<>();
					for (BatchDetails batch : batches) {
						Map<String, Object> batchData = new HashMap<>();
						batchData.put("batchCode", batch.getBatchCode());
						batchData.put("batchStartingDate", batch.getBatchStartingDate());
						batchData.put("batchEndingDate", batch.getBatchEndingDate());
						responseDetails.put(batch.getBatchName(), batchData);
					}
					response.put("details", responseDetails);
					response.put("status", "Success");
					response.put("message", "batch details fetched successfully");
					return response;

				}

			}

		} catch (Exception e) {
			logger.info("Error occured", e);
			response.put("status", "Error");
			response.put("message", "exception error occured");
			return response;
		}
	}

	// get coaches from a batch method
	public Map<String, Object> getCoachesFromaBatch(HashMap<String, Object> data, String userToken) {
		Map<String, Object> response = new HashMap<>();
		try {
			// checking all parameters coming or not
			@SuppressWarnings("unchecked")
			HashMap<String, Object> parameters = (HashMap<String, Object>) data.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> userCredentials = (HashMap<String, Object>) data.get("userCredentials");
			if (parameters.get("batch") == null || userCredentials.get("userId") == null
					|| userCredentials.get("userId").toString().isEmpty() || userToken == null || userToken.isEmpty()) {
				response.put("status", "Error");
				response.put("message", "all parameters required");
				return response;

			}
			// checking token and user credentials valid or not
			UserCredentials user = userCredentialRepository
					.findByUserId(Long.parseLong(userCredentials.get("userId").toString().trim()));
			boolean token = Boolean.parseBoolean(userToken);
			if (user == null) {
				response.put("status", "Error");
				response.put("message", "User doesn't exist");
				return response;
			} else if (!token) {
				response.put("status", "Error");
				response.put("message", "invalid token");
				return response;
			}

			@SuppressWarnings("unchecked")
			HashMap<String, Object> batch = (HashMap<String, Object>) parameters.get("batch");
			// checking batch parameters coming or not
			if (batch.get("batchId") == null || batch.get("batchId").toString().trim().isEmpty()) {
				response.put("status", "Error");
				response.put("message", "all parameters required");
				return response;
			}
			// checking the batch id is valid or not
			if (batchDetailsRepository
					.getBatchByBatchId(Long.parseLong(data.get("batchId").toString().trim())) == null) {
				response.put("status", "Error");
				response.put("message", "Invalid batch code");
				return response;
			} else {
				// checking the player assigned to a batch or not
				if (!assignToBatchRepository
						.batchHaveCoachesOrNot(Long.parseLong(data.get("batchId").toString().trim()))) {
					response.put("status", "Error");
					response.put("message", "There is no coaches assigned to this batch");
					return response;
				} else {
					List<UserPersonalDetails> coaches = assignToBatchRepository
							.findCoachDetailsByBatchCode(Long.parseLong(data.get("batchId").toString().trim()));
					Map<String, Object> responseDetails = new HashMap<>();
					// getting each player
					for (UserPersonalDetails coach : coaches) {
						Map<String, Object> coachData = new HashMap<>();
						coachData.put("coachEmail", coach.getEmail());
						coachData.put("coachPhoneNumber", coach.getPhoneNumber());

						responseDetails.put(coach.getFullName(), coachData);
					}
					response.put("details", responseDetails);
					response.put("status", "Success");
					response.put("message", "coaches details  fetched successfully ");
					return response;
				}

			}

		} catch (Exception e) {
			logger.info("Error occured", e);
			response.put("status", "Error");
			response.put("message", "exception error occured");
			return response;
		}
	}

	// batch code auto generation
	public String generateBatchCode(String batchType, String batchName) {
		String prefix = "PEN"; // Fixed prefix represent peninsula
		String yearCode = String.valueOf(Year.now().getValue()).substring(2); // Last two digits of current year
		String batchTypeCode = (batchType != null && !batchType.isEmpty()) ? batchType.substring(0, 1).toUpperCase()
				: "X";// first letter of batch type
		String nameCode = (batchName != null && batchName.length() >= 2) ? batchName.substring(0, 2).toUpperCase()
				: "XX";// first two letter of batch name

		return String.format("%s%s%s%s", prefix, yearCode, batchTypeCode, nameCode);
	}

}
