package com.gms_worldwide.hybersdk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew Kochura.
 */
class HyberMessagesEnvelope {

    private boolean status;
    private String response;
    private List<HyberMessageResponseModel> messages;

    /**
     * Instantiates a Hyber messages envelope.
     *
     * @param messages the messages
     */
    public HyberMessagesEnvelope(List<HyberMessageResponseModel> messages) {
        this.messages = messages;
    }

    /**
     * Gets messages.
     *
     * @return the messages
     */
    public List<HyberMessageModel> getMessages() {
        List<HyberMessageModel> appMessageModels = new ArrayList<>();
        for (HyberMessageResponseModel responseModel : messages) {
            appMessageModels.add(new HyberMessageModel(responseModel.getId(), responseModel.getFrom(),
                    responseModel.getMessage(), responseModel.getTime(),
                    responseModel.getType(), responseModel.getOwner(), false));
        }
        return appMessageModels;
    }

    /**
     * Is status boolean.
     *
     * @return the boolean
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Gets response.
     *
     * @return the response
     */
    public String getResponse() {
        return response;
    }
}
