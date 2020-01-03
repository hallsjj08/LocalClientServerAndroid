package com.jjas.clientserver;

public interface OnFragmentInteractionListener {

    /**
     * A callback to handle communication between the hosting activity and the options selected
     * in a fragment.
     * @param request such as {@link NetworkService#SETUP_HOST_SERVER}
     */
    void networkServiceRequest(int request, String arg);

}
